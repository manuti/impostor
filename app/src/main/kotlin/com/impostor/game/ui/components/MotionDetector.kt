package com.impostor.game.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt

/**
 * Detecta movimiento del dispositivo que debe ocultar contenido privado:
 * aceleración lineal brusca (sacudida) **o** rotación sostenida (girar el
 * móvil para pasarlo a otra persona). Regla de fase 4 (§5.2 del plan):
 * sensor aislado en componente reutilizable, ciclo de vida gestionado con
 * DisposableEffect y sin lógica de negocio dentro — solo notifica [onMotion].
 *
 * Usa dos sensores de refuerzo, cada uno con su propio listener:
 * - TYPE_LINEAR_ACCELERATION (m/s², sin gravedad): detecta el gesto brusco.
 * - TYPE_GYROSCOPE (rad/s): detecta el giro del paso entre jugadores, que el
 *   acelerómetro apenas ve cuando el movimiento es suave (calibración 2026-09-02).
 *
 * Los listeners NUNCA acceden a `event.sensor`: tras unregisterListener el
 * sistema puede entregar eventos en cola con `sensor == null`, y leer
 * `event.sensor.type` lanzaría NullPointerException.
 *
 * Usa SENSOR_DELAY_GAME (≈50 Hz), no FASTEST: desde Android 12 (API 31) una
 * tasa de 0 µs (FASTEST) exige declarar HIGH_SAMPLING_RATE_SENSORS en el
 * manifest y lanza SecurityException sin ella (causa del cuelgue 2026-09-02).
 * GAME evita añadir permisos (fase 4) y basta: 3 muestras ≈ 60 ms de gesto
 * sostenido, y el giro de paso dura cientos de ms.
 *
 * Se registran solo cuando [enabled] es true. Ventana de muestras consecutivas
 * sobre el umbral por canal (filtra picos aislados); dispara cuando cualquiera
 * de los dos la completa, con intervalo mínimo entre avisos.
 */
@Composable
fun MotionDetector(
    enabled: Boolean,
    onMotion: () -> Unit,
    linearThreshold: Float = LINEAR_THRESHOLD_M_S2,
    gyroThreshold: Float = GYRO_THRESHOLD_RAD_S,
    minIntervalMs: Long = MIN_MOTION_INTERVAL_MS,
) {
    val context = LocalContext.current
    val currentOnMotion by rememberUpdatedState(onMotion)

    DisposableEffect(enabled) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val linearAccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (!enabled || (linearAccel == null && gyroscope == null)) {
            onDispose {}
        } else {
            // Estado compartido por ambos canales: contadores por canal y
            // momento de la última detección (evita avisos duplicados).
            var lastMotionAt = 0L
            var linearSamplesOver = 0
            var gyroSamplesOver = 0

            fun fireIfReady() {
                val now = SystemClock.elapsedRealtime()
                val detected =
                    (linearSamplesOver >= REQUIRED_SAMPLES || gyroSamplesOver >= REQUIRED_SAMPLES) &&
                        now - lastMotionAt > minIntervalMs
                if (detected) {
                    linearSamplesOver = 0
                    gyroSamplesOver = 0
                    lastMotionAt = now
                    currentOnMotion()
                }
            }

            val linearListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val magnitude = magnitudeOf(event)
                    linearSamplesOver = if (magnitude > linearThreshold) linearSamplesOver + 1 else 0
                    fireIfReady()
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }

            val gyroListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val magnitude = magnitudeOf(event)
                    gyroSamplesOver = if (magnitude > gyroThreshold) gyroSamplesOver + 1 else 0
                    fireIfReady()
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }

            if (linearAccel != null) {
                sensorManager.registerListener(linearListener, linearAccel, SensorManager.SENSOR_DELAY_GAME)
            }
            if (gyroscope != null) {
                sensorManager.registerListener(gyroListener, gyroscope, SensorManager.SENSOR_DELAY_GAME)
            }
            onDispose {
                if (linearAccel != null) sensorManager.unregisterListener(linearListener)
                if (gyroscope != null) sensorManager.unregisterListener(gyroListener)
            }
        }
    }
}

private fun magnitudeOf(event: SensorEvent): Float {
    val x = event.values[0]
    val y = event.values[1]
    val z = event.values[2]
    return sqrt(x * x + y * y + z * z)
}

/**
 * Umbral de aceleración lineal (m/s²). Calibrado en dispositivo: 12 y 6 exigían
 * un gesto demasiado brusco; 4 funcionaba pero aún fuerte; 3 + giroscopio cubre
 * el paso suave entre jugadores (2026-09-02).
 */
private const val LINEAR_THRESHOLD_M_S2 = 3f

/**
 * Umbral de velocidad angular (rad/s) del giroscopio. El giro de pasar el móvil
 * entre dos personas supera holgadamente 2 rad/s; el temblor de mano al leer
 * (oscilatorio, baja amplitud) no lo alcanza de forma sostenida.
 */
private const val GYRO_THRESHOLD_RAD_S = 2f

/** Muestras consecutivas sobre el umbral (en un canal) para confirmar el movimiento. */
private const val REQUIRED_SAMPLES = 3

/** Intervalo mínimo (ms) entre detecciones para evitar repeticiones. */
private const val MIN_MOTION_INTERVAL_MS = 600L
