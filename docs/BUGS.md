# Bugs conocidos (fases 2 y 2.5)

> Registro abierto el **2026-08-26** tras la primera prueba en dispositivo del usuario.
> Correcciones previstas: **2026-08-27**. Mientras tanto **no publicar el APK v0.3.0 como release**.
> Fase 2 cerrada. Registro continúa en fase 2.5 (v0.3.x): BUG-5 (2026-08-31), BUG-6 (2026-09-02).

## BUG-1 — CRÍTICO: fuga visual del rol al pasar de jugador (animación del naipe)

- **Severidad**: crítica (rompe el secreto del juego: se ve el rol del siguiente jugador).
- **Síntoma**: al pulsar "Siguiente Jugador" (o "Empezar Partida" en el último), la nueva carta se ve por una fracción de segundo antes de girarse — suficiente para desvelar al impostor o la palabra.
- **Causa (diagnóstico en código)**: al cambiar `currentPlayerIndex`, `revealed` se reinicia a `false` (`remember(currentPlayerIndex)`), pero la animación `animateFloatAsState` hace la transición 180°→0° (500 ms). Mientras la rotación es > 90° se sigue mostrando el **reverso**, que ya contiene el contenido del **nuevo** jugador (palabra/pista). La vuelta animada al anverso "desvela" el rol del siguiente.
- **Fix propuesto**: el naipe debe volver al anverso **sin animación** al cambiar de jugador:
  - `Animatable` + `snapTo(0f)` en `LaunchedEffect(currentPlayerIndex)`, animando solo la revelación (anverso→reverso); y/o
  - no componer el reverso hasta que la rotación > 90° **y** el estado sea `revealed` del jugador actual (contenido con key por `currentPlayer.id`).
- **Fix aplicado (2026-08-28)**: el naipe usa un `Animatable` con key por `currentPlayerIndex` — al cambiar de jugador nace ya en 0° (anverso) **sin transición**, por lo que el reverso con el contenido del siguiente jugador nunca se compone en el cambio. La animación queda solo para el toque (revelar/ocultar): `scope.launch { rotation.animateTo(...) }`.
- **Estado**: ✅ **corregido y verificado en dispositivo (2026-08-28)**.

## BUG-2 — Número de impostores ilegible en tema oscuro

- **Severidad**: media (legibilidad).
- **Síntoma**: en modo oscuro, el número del stepper de impostores se ve negro sobre fondo gris oscuro.
- **Causa raíz (confirmada en código)**: `contentColorFor()` de Material 3 usa el **tema del sistema**, no el de la app. Con la app en oscuro y el sistema en claro, `LocalContentColor` se resuelve a negro → el número (sin color explícito) salía negro sobre el fondo oscuro.
- **Fix aplicado (2026-08-28)**: (1) en `ImpostorTheme` se fija `LocalContentColor = colorScheme.onBackground` (corrige todos los textos/iconos que dependen del color por defecto, p. ej. el toggle sol/luna); (2) el número del stepper y los botones −/+ usan colores explícitos (incluido el estado deshabilitado).
- **Estado**: 🔄 corregido en código — pendiente de verificar en dispositivo (APK nuevo en Descargas).

## BUG-3 — El ojo del anverso del naipe es demasiado pequeño

- **Severidad**: baja (estética / fidelidad al diseño).
- **Síntoma / requisito**: el logo del ojo debe ser **enorme y ocupar el máximo posible de la carta**; hoy es un icono fijo de 84dp.
- **Fix aplicado (2026-08-28)**: el anverso se rediseñó — el ojo ahora ocupa el **máximo posible de la carta** (vector redibujado sin márgenes + `fillMaxWidth(0.96f)` en la zona disponible, dejando solo "TOCA PARA VER" legible abajo).
- **Estado**: 🔄 corregido en código — pendiente de verificar en dispositivo (APK nuevo en Descargas).

## BUG-4 — Pupila del ojo poco visible en tema claro

- **Severidad**: baja/media (contraste).
- **Síntoma**: en modo claro, la pupila ámbar (`#F59E0B`) sobre el anverso blanco tiene poco contraste.
- **Fix aplicado (2026-08-28, junto con BUG-3)**: el ojo se dibuja con dos colores por tema en `GameColors` (`eye` + `eyePupil`): en oscuro, ojo claro con pupila índigo; en claro, ojo índigo con pupila ámbar (marca de la app). Contraste AA en ambos temas.
- **Estado**: 🔄 corregido en código — pendiente de verificar en dispositivo (APK nuevo en Descargas).

## BUG-5 — El pie "Pasa el móvil" repetía el nombre del jugador activo en vez del siguiente (M-2, v0.3.3)

- **Severidad**: media (confunde la mecánica de paso del móvil).
- **Síntoma**: en la revelación de rol, el pie decía "Pasa el móvil a NOMBRE" con el nombre del jugador **activo** (el que acaba de ver su rol, repetido de la píldora), cuando debe indicar a **quién** hay que pasarle el móvil (el siguiente jugador).
- **Causa (diagnóstico en código)**: M-2 usó `currentPlayer.name` en el pie.
- **Fix aplicado (2026-08-31)**: el pie usa `players[currentPlayerIndex + 1].name` (el siguiente jugador); para el último jugador no se muestra, porque no hay a quién pasarle el móvil (el botón pasa a ser "Empezar Partida") — esto también corrige el defecto latente de la fase 2, que mostraba el texto genérico también en el último jugador.
- **Estado**: ✅ **corregido y verificado en dispositivo (2026-08-31)**.

## BUG-6 — CRÍTICO: cuelgue al revelar el naipe por SENSOR_DELAY_FASTEST sin permiso (M-1, v0.3.4)

- **Severidad**: crítica (crash al activar el sensor de movimiento).
- **Síntoma**: al empezar la partida y revelar la carta (momento en que el sensor se registra), la app crasheaba.
- **Causa raíz (stacktrace en dispositivo)**: `java.lang.SecurityException: To use the sampling rate of 0 microseconds, app needs to declare the normal permission HIGH_SAMPLING_RATE_SENSORS` — `SENSOR_DELAY_FASTEST` pide 0 µs y, desde Android 12 (API 31), exige declarar ese permiso en el manifest.
- **Fix aplicado (2026-09-02)**: `SENSOR_DELAY_GAME` (~50 Hz), sin permiso nuevo en el manifest (mejor para la fase 4). Suficiente: la ventana de 3 muestras ≈ 60 ms y el gesto de paso dura cientos de ms. Además se dejó un listener por sensor sin acceder a `event.sensor` (evita NPE si llegan eventos en cola tras el unregister).
- **Calibración del umbral (misma fecha)**: acelerómetro lineal 3 m/s² + giroscopio 2 rad/s (el giro de pasar el móvil entre jugadores no lo ve el acelerómetro solo).
- **Estado**: ✅ **corregido y verificado en dispositivo (2026-09-02)**.

## Resumen

| Bug | Severidad | Estado |
|---|---|---|
| BUG-1 · fuga visual del rol | Crítica | ✅ corregido y verificado (2026-08-28) |
| BUG-2 · stepper de impostores en oscuro | Media | ✅ corregido y verificado (2026-08-28) |
| BUG-3 · ojo del naipe pequeño | Baja | ✅ corregido y verificado (2026-08-28) |
| BUG-4 · pupila en tema claro | Baja | ✅ corregido y verificado (2026-08-28) |
| BUG-5 · pie repite el nombre del jugador activo | Media | ✅ corregido y verificado (2026-08-31) |
| BUG-6 · crash por SENSOR_DELAY_FASTEST sin permiso | Crítica | ✅ corregido y verificado (2026-09-02) |
