# Plan de la fase 4 — Publicación en Google Play

> Documento de arranque de la fase 4 (publicación en Google Play), creado el **2026-09-02** al cerrar la fase 2.5, con las lecciones aprendidas en las fases 2 y 2.5.
> La fase 4 **no ha empezado**: este documento fija el punto de partida, las lecciones y el checklist de la tienda.

---

## 1. Estado actual al inicio de la fase 4

- App publicada hasta **v0.3.4** (release en GitHub con APK `impostor-v0.3.4.apk`). Rama `main`, historial limpio.
- Fases cerradas: 1 (MVP v0.1.0/v0.2.0), 2 (usabilidad v0.3.0), 2.5 (mejoras intermedias v0.3.1–v0.3.4: reordenación de configuración, toggle de tema en todas las pantallas, jugador activo destacado, carta oculta al mover el dispositivo).
- **Permisos declarados: ninguno** en el manifest. Es un estado ideal para Play Store — mantenerlo mientras sea posible.
- Licencia: CC BY-NC 4.0. La app se distribuiría gratuita (uso no comercial) — coherente con la licencia; revisar los términos de Play sobre contenido gratuito al publicar.
- Repo público: `manuti/impostor`. Release actual: v0.3.4.

## 2. Lecciones de la fase 2 (usabilidad, v0.3.0)

1. **Probar en dispositivo real desde el primer cambio** — no hay emulador; cada ajuste de UI, contraste o target táctil se decide con la prueba real del usuario.
2. **Ciclo corto por cambio**: implementar → compilar → copiar APK a Descargas → probar en dispositivo → solo con confirmación: commit + push + release. Nunca releasear sin prueba del usuario.
3. **i18n-ready desde el inicio**: todas las cadenas en `res/values/strings.xml` con placeholders `%1$s` y `<plurals>`; prohibido hardcodear strings en Kotlin. Esto despeja la fase 3 y evita reescrituras.
4. **Criterios por cambio**: contraste AA, targets ≥ 48dp, `contentDescription` (TalkBack), sin strings nuevos hardcodeados (grep o revisión del diff).
5. **Registro de bugs disciplinado**: `docs/BUGS.md` con severidad, síntoma, causa raíz (diagnóstico en código), fix aplicado con fecha y verificación en dispositivo. Incluye bugs de fase (BUG-1…BUG-4).

## 3. Lecciones de la fase 2.5 (v0.3.1–v0.3.4)

1. **Sensores** (M-1): cualquier sensor debe vivir en un componente aislado y reutilizable (`ui/components/MotionDetector.kt`), con ciclo de vida gestionado por `DisposableEffect` y **sin lógica de negocio dentro** — solo notifica un callback. La pantalla decide qué hacer.
2. **`SENSOR_DELAY_FASTEST` (0 µs) exige `HIGH_SAMPLING_RATE_SENSORS`** desde Android 12 (API 31): sin el permiso declarado, `registerListener` lanza `SecurityException` y crashea (BUG-6). **Decisión: usar `SENSOR_DELAY_GAME`** (~50 Hz) — suficiente para ventanas de 3 muestras y sin permisos nuevos. Si algún día se necesita FASTEST, declarar el permiso normal en el manifest.
3. **No acceder a `event.sensor`** en los listeners: tras `unregisterListener` el sistema puede entregar eventos en cola con `sensor == null` (NPE). Usar un listener por sensor, cada uno con su umbral, leyendo solo `event.values`.
4. **Calibrar umbrales en el dispositivo real** (nunca por teoría): acelerómetro lineal 12 → 6 → 4 → 3 m/s² hasta encontrar el gesto natural; el **giroscopio** (2 rad/s) es el sensor clave para detectar el giro de pasar el móvil entre jugadores, que el acelerómetro no ve cuando el movimiento es suave.
5. **Release con assets nombrados**: subir el APK ya renombrado como `impostor-vX.Y.Z.apk` (la sintaxis `archivo#nombre` de `gh release create` **no renombra** — verificado 2026-08-31; subir la copia renombrada y borrar el asset mal nombrado).
6. **Versionado**: incremento de patch por mejora + `versionCode` +1 en el mismo commit del release. El APK de Descargas se nombra con la versión objetivo desde el primer build de prueba.
7. **El usuario detecta bugs de UX que el código no revela** (BUG-5: el pie repetía el nombre del jugador activo en vez de indicar al siguiente). Validar siempre el flujo real completo en la prueba en dispositivo.

## 4. Reglas vinculantes heredadas (PLAN-FASE2.5 §5)

- Modelos como datos puros; acciones como funciones puras sobre `GameState`.
- Sensores/periféricos aislados en componentes reutilizables con ciclo de vida gestionado (`DisposableEffect`), sin lógica de negocio dentro (ya cumplido con `MotionDetector`).
- **No añadir analytics/telemetría en esta fase** (telemetrydeck se evalúa en fase 4, con su política de consentimiento y declaración de privacidad).
- Cambios pequeños; compilar y probar en dispositivo tras cada uno; UI en es-419 hasta la fase 3 (i18n).

## 5. Checklist de requisitos de Google Play (a completar en la fase 4)

- [ ] **Cuenta de desarrollador** de Google Play (pago único; revisar tarifa vigente) y acceso a Play Console.
- [ ] **Manifest/permisos**: mantener **cero permisos** (estado actual). Si se añade alguno, justificarlo en la ficha (declaración de permisos de Play).
- [ ] **targetSdk 36** (ya configurado) — comprobar el requisito mínimo vigente de Play en el momento de publicar.
- [ ] **App Bundle (AAB)** para producción (`bundleRelease`), no APK; firmado con keystore de release (crear y custodiar; no versionar).
- [ ] **Icono adaptable** y recursos de ficha: capturas de pantalla (mín. 2 teléfono), gráfica destacada, título y descripciones corta/larga en es-419 (mismas reglas de estilo que la app: ortografía correcta, p. ej. **píldora**, no *pillula*).
- [ ] **Clasificación de contenido** (cuestionario IARC) y confirmación de políticas (contenido, spam, datos).
- [ ] **Política de privacidad**: la app no recoge datos (todo local). Publicar URL de política aunque no haya recogida, según los requisitos vigentes.
- [ ] **Testing**: subir primero a *internal testing* con la lista de probadores; validar instalación desde Play (firma distinta a la de debug).
- [ ] **Cumplimiento de calidad**: sin crashes en el flujo completo (lección BUG-6), rendimiento, comportamiento sin red (la app es 100 % local).
- [ ] **Licencia y atribuciones**: revisar coherencia de CC BY-NC con la distribución gratuita en Play; atribuciones de la referencia React en la ficha o en "Acerca de".
- [ ] **Versionado de publicación**: mantener `versionCode` creciente; el `versionName` de Play puede seguir `v0.4.x` o saltar a `1.0.0` (decisión de la fase 4).
- [ ] **Telemetrydeck** (solo si se decide): integrar como componente aislado, con consentimiento y su declaración en la política de privacidad.

## 6. Cambios de la fase 4 (antes de publicar en Play) — a detallar

Orden previsto; **el primero en implementarse**:

### C-1 · No repetir la palabra secreta entre partidas consecutivas (primer cambio)

- **Qué**: al empezar una partida nueva, evitar que la palabra secreta coincida con la de la partida anterior. Hoy `getRandomWord` usa `pool.random()` sin memoria (`GameWords.kt`): con una categoría de 10 palabras, repetir la anterior tiene 1/10 de probabilidad; en varias partidas seguidas es muy probable.
- **Cómo**: recordar la última palabra elegida y excluirla del pool en la siguiente selección (si el pool tiene más de 1 palabra; si solo queda 1, permitir repetir). Basta en memoria por sesión (`App.kt`, en `startGame`); opcional: persistirla en SharedPreferences para que sobreviva al cierre de la app.
- **Toca**: `game/GameWords.kt` (nueva función de selección con exclusión o parámetro) y `ui/App.kt` (pasar/excluir la última palabra). No cambia la mecánica de juego ni añade dependencias.
- **Verificación**: jugar dos partidas seguidas con la misma categoría → la palabra no se repite; pool de 1 palabra → permite repetir; sin regresión en el flujo normal.

Y el resto del trabajo previsto (a detallar al llegar):
- Animación de inicio / splash.
- Evaluación de telemetrydeck (decisión explícita pendiente).
- Revisión final de accesibilidad (TalkBack) en la ficha y en la app.
- Preparación de materiales de la ficha de Play (textos, capturas, icono).

## 7. Post-publicación — mejoras futuras propuestas

> Añadido 2026-09-02 por el usuario. NO implementar en la fase 4: son candidatas a una fase posterior a la publicación en Google Play. Al arrancar cualquiera, moverla a su propio plan con detalle.

### T-1 · Revelado de rol por audio al acercar el móvil al oído

- **Qué**: alternativa (o complemento) auditiva al revelado visual: el jugador acerca el teléfono a la oreja y el TTS le dice su rol en privado — "NOMBRE, tu palabra es X" (civil) o "NOMBRE, eres impostor" (+ su pista si está activada).
- **Por qué**: permite jugar sin mirar la pantalla; beneficia a personas con dificultad visual (misma audiencia que la fase 2) y a quien prefiera no leer.
- **Detección de "acercar a la oreja"**: sensor de proximidad (`TYPE_PROXIMITY`) — componente aislado y reutilizable con `DisposableEffect`, sin lógica de negocio, siguiendo el precedente de `MotionDetector` (M-1, v0.3.4). Sin permisos nuevos (la proximidad no requiere permiso).
- **Audio**: `TextToSpeech` del sistema. Probar en dispositivo que la voz sale por el **auricular/earpiece** (o a volumen bajo) para que solo la oiga el jugador; silenciar al retirar el móvil de la oreja.
- **⚠️ Cambio de decisión previa**: las reglas de TTS de PLAN-FASE3 §6 y CONTEXTO-FASE2 §6.6 descartan leer palabra/pista "nunca sobre contenido secreto". Esta tarea lo revierte **solo en el modo privado** (móvil al oído). Al implementarla: actualizar esos documentos y fijar la condición (proximidad activa) como única vía de lectura del secreto.
- **i18n (fase 3)**: la voz sigue el locale; la lectura usa cadenas traducidas con placeholders (p. ej. "%1$s, tu palabra es %2$s").
- **Verificación futura en dispositivo**: al acercar el móvil a la oreja con la carta revelada se oye el rol; al retirarlo se silencia; los demás jugadores no oyen nada; el revelado visual actual no cambia.
- **Dependencias**: fase 3 (i18n) cerrada, para que la voz y las cadenas estén localizadas.
