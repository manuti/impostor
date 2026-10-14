# Bugs conocidos (fases 2, 2.5, 3 y 4)

> Registro abierto el **2026-08-26** tras la primera prueba en dispositivo del usuario.
> Fase 2 cerrada (v0.3.0, publicada el **2026-08-28**). El registro continúa con las fases 2.5 (BUG-5, BUG-6), 3 (BUG-7) y 4 (BUG-8 a BUG-11).
>
> *Nota histórica*: el aviso original «no publicar el APK v0.3.0 como release» quedó resuelto al verificarse los bugs en dispositivo y publicarse v0.3.0 el 2026-08-28.

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
- **Estado**: ✅ **corregido y verificado en dispositivo (2026-08-28)**.
- **Nota (2026-10-14)**: esta ficha se quedó con el estado «pendiente de verificar» por descuido, mientras la tabla resumen y `BUG-1` ya decían «verificado (2026-08-28)» en el **mismo commit** que creó el archivo (`e189809`, el propio tag `v0.3.0`). Se reconcilia con la tabla: la fase 2 cerró y v0.3.0 se publicó el 2026-08-28, y según la regla del proyecto (PLAN-FASE4 §2) no se publica sin prueba previa en dispositivo.

## BUG-3 — El ojo del anverso del naipe es demasiado pequeño

- **Severidad**: baja (estética / fidelidad al diseño).
- **Síntoma / requisito**: el logo del ojo debe ser **enorme y ocupar el máximo posible de la carta**; hoy es un icono fijo de 84dp.
- **Fix aplicado (2026-08-28)**: el anverso se rediseñó — el ojo ahora ocupa el **máximo posible de la carta** (vector redibujado sin márgenes + `fillMaxWidth(0.96f)` en la zona disponible, dejando solo "TOCA PARA VER" legible abajo).
- **Estado**: ✅ **corregido y verificado en dispositivo (2026-08-28)** (misma nota de reconciliación que BUG-2).

## BUG-4 — Pupila del ojo poco visible en tema claro

- **Severidad**: baja/media (contraste).
- **Síntoma**: en modo claro, la pupila ámbar (`#F59E0B`) sobre el anverso blanco tiene poco contraste.
- **Fix aplicado (2026-08-28, junto con BUG-3)**: el ojo se dibuja con dos colores por tema en `GameColors` (`eye` + `eyePupil`): en oscuro, ojo claro con pupila índigo; en claro, ojo índigo con pupila ámbar (marca de la app). Contraste AA en ambos temas.
- **Estado**: ✅ **corregido y verificado en dispositivo (2026-08-28)** (misma nota de reconciliación que BUG-2).

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

## BUG-7 — Al cambiar el idioma se perdían los jugadores añadidos (i18n, v0.4.0)

- **Severidad**: media (pérdida de datos introducidos por el usuario, con riesgo de confusión en mitad de una partida).
- **Síntoma**: al usar el selector de idioma de la app, se aplicaba la nueva traducción pero **desaparecían los jugadores ya añadidos y la configuración de la partida** (categoría, impostores, pista).
- **Causa raíz (diagnóstico en código)**: `AppCompatDelegate.setApplicationLocales(...)` aplica el locale **recreando la actividad**; el estado de la partida vive en memoria (`remember` de Compose) y no se persiste, así que la recreación lo descarta.
- **Fix aplicado (2026-09-11)**: `LanguageButton(hasPlayers)` muestra un diálogo de confirmación (`language_change_title` / `language_change_text` / `language_change_confirm`) antes de cambiar el idioma si ya hay jugadores; sin jugadores (lista vacía) el cambio se aplica directo, sin fricción. Alternativa futura, si se quiere no perder nada: `rememberSaveable` o persistir el borrador de la partida.
- **Estado**: ✅ **corregido y verificado en dispositivo (2026-09-11)**.

## BUG-8 — DESCARTADO: el color de la carta de rol NO delataba al impostor (v0.4.1)

- **Estado**: ❌ **no es un bug** — hipótesis descartada el 2026-10-14 leyendo el código, antes de publicar. Se conserva el registro por su valor como lección (ver BUG-10).
- **Hipótesis inicial**: el reverso de la carta se pintaba según el rol (`impostorBg` rojo / `civilBg` azul), así que quien viera el móvil al pasarlo sabría si su dueño era el impostor sin leer nada.
- **Por qué es falsa**: `FlipCard` solo compone el reverso cuando `rotation > 90°`, es decir con la carta **ya girada hacia su dueño**; el color aparece en el mismo fotograma que el texto del rol («ERES IMPOSTOR»), ante los mismos ojos, así que no añade exposición. Lo único visible con la carta cerrada —borde exterior (`cardFrontBorder`), anverso (`cardFrontBg` + ojo) y píldora del nombre (`primaryContainer`)— es neutro. En el cambio de jugador la rotación nace en 0° y el detector de movimiento devuelve la carta al anverso neutro, así que el reverso del siguiente jugador nunca se compone (patrón del BUG-1).
- **Lección**: un elemento puede codificar el rol sin filtrarlo, y la pregunta que decide no es «¿usa color por rol?» sino **«¿es visible antes de girar la carta?»**. Esa frontera quedó fijada en el ADR 17, que supersede al ADR 16 (que unificó el color con el análisis equivocado).

## BUG-9 — Contrastes por debajo de AA en tema claro (v0.4.1)

- **Severidad**: media (legibilidad en el perfil objetivo: baja visión).
- **Síntoma / medición** (fórmula WCAG 2.x, calculado el 2026-10-14):
  - burbujas de nombre de `EndGameScreen`, tema claro: `impostor` `#B91C1C` sobre el fondo rojizo compuesto ≈**3,9:1** (por debajo de 4,5:1).
  - avisos con `warn` claro `#B45309` sobre blanco: **4,94:1** (cumplía, pero sin margen, en texto de 16 sp).
- **Causa raíz**: tintas con alfa sobre fondos ya teñidos — las alfas se componen y el contraste real cae por debajo del nominal — y un ámbar demasiado claro para texto.
- **Fix aplicado (2026-10-14)**: las burbujas usan el par semántico completo (`impostorBg`/`onImpostorBg` y `civilBg`/`onCivilBg`): ≈8,6:1 y ≈9:1 en claro, ≈10,8:1 y ≈11:1 en oscuro. `warn` claro pasa a `#92400E` (≈6,97:1). Verificados además `civil` `#1D4ED8` (6,59:1), `impostor` `#B91C1C` (6,36:1) y `success` `#15803D` (4,93:1) sobre `background` en claro, y las etiquetas de fase sobre `surfaceVariant` (≈9:1 oscuro / ≈8,3:1 claro).
- **Estado**: ✅ **corregido y verificado en dispositivo (2026-10-14, Android 16)** — el usuario confirmó que los colores y contrastes se ven bien.

## BUG-10 — La unificación del color de la carta dejó el rol sin señal identificativa (v0.4.1)

- **Severidad**: media (usabilidad en el perfil objetivo: baja visión).
- **Síntoma**: la carta del impostor y la del civil quedaban **idénticas** salvo el texto de la cabecera; para saber si eras el impostor había que leer una frase, en el momento de mayor carga cognitiva del juego. La cabecera del civil, además, decía «TU PALABRA ES» en mayúsculas y quedaba descuadrada.
- **Texto de la cabecera del civil (2026-10-14)**: pasa a `role_your_word` = «La palabra es» / «The word is», centrada y en minúscula inicial (antes «TU PALABRA ES»).
- **Origen**: el punto 3 del ticket estético pedía unificar el color de la carta porque las capturas sugerían que los tonos eran «decorativos/aleatorios». La premisa era incorrecta (el código sí usaba el token del rol) y el ADR 16 aplicó la unificación. Lo detectó el usuario al preguntar por qué el fondo del impostor era igual al de la palabra (2026-10-14).
- **Fix aplicado (2026-10-14)**: el reverso vuelve a teñirse por rol (`impostorBg`/`civilBg` con `onImpostorBg`/`onCivilBg`). Se probó añadir **además** un icono de rol en línea con la cabecera (antifaz para el impostor, tarjeta con texto para el civil) y **se retiró el mismo día** por indicación del usuario: el color ya distingue y el icono descuadraba el centrado de la cabecera, así que la carta usa dos canales (color + texto) y no tres. Contrastes medidos: blanco sobre `impostorBg` ≈10,1:1 (oscuro) / `onImpostorBg` sobre `impostorBg` ≈8,6:1 (claro); `onCivilBg` sobre `civilBg` ≈10,4:1 (oscuro) / ≈9,1:1 (claro).
- **Lo que se mantiene neutro** (y aquí el ticket acertaba): el borde exterior, el anverso y la píldora del nombre del jugador activo, visibles con la carta cerrada. Si codificaran el rol, la fuga sería real.
- **Estado**: ✅ **corregido y verificado en dispositivo (2026-10-14, Android 16)** — confirmado que el color ya distingue impostor/civil, la cabecera «La palabra es …» queda centrada y no hace falta icono.

## BUG-11 — CRÍTICO: girar el móvil reiniciaba la partida (v0.4.1)

- **Severidad**: crítica (pérdida total de la partida en curso desde cualquier fase).
- **Síntoma**: al girar el móvil, la app volvía a la pantalla de configuración con la partida perdida, en cualquier fase (reparto, debate o final).
- **Causa raíz (diagnóstico en código)**: la actividad no declaraba `configChanges`, así que un cambio de configuración (el giro) **recrea la actividad**. El estado de la partida vive en memoria de composición —`var gameState by remember { mutableStateOf(GameState()) }` en `ui/App.kt`— y `remember` **no sobrevive a la recreación**, de modo que se reinicializa a `GameState()`, cuya fase por defecto es `SETUP`. El tema sí se salvaba porque usa `rememberSaveable`. Es la misma familia que el BUG-7 (el cambio de idioma recrea la actividad), pero entonces no se detectó que el giro tuviera el mismo efecto.
- **Fix aplicado (2026-10-14)**: en `AndroidManifest.xml`, la actividad pasa a `android:screenOrientation="portrait"` (el giro no debe hacer nada) **más** `android:configChanges="orientation|screenSize|screenLayout|smallestScreenSize|keyboardHidden"`. El segundo es necesario porque **desde Android 16 (API 36) el sistema ignora el bloqueo de orientación en pantallas de 600 dp o más de ancho**: sin declarar `configChanges`, girar en una tablet recrearía la actividad igual y seguiría perdiéndose la partida. Con `configChanges` la actividad no se recrea (Compose vuelve a medir y el estado se conserva) y el bloqueo en vertical queda como la vía que hace que «no pase nada» en el teléfono.
- **Efecto lateral verificado**: el APK declara ahora el rasgo implícito `android.hardware.screen.portrait` (`aapt2 dump badging`), lo que Play usa para filtrar dispositivos sin pantalla vertical. Es coherente con un juego de pasar el móvil de mano en mano.
- **Estado**: ✅ **corregido y verificado en dispositivo (2026-10-14, Android 16)** — girar no hace nada y no reinicia la partida en ninguna fase. **Salvedad**: en una tablet (≥600 dp) con Android 16 el sistema ignora el bloqueo de orientación y el layout **sí** rota; lo garantizado es que la partida no se pierde (`configChanges` evita la recreación). Pendiente de comprobar en tablet si hay acceso a una.

## Resumen

| Bug | Severidad | Estado |
|---|---|---|
| BUG-1 · fuga visual del rol | Crítica | ✅ corregido y verificado (2026-08-28) |
| BUG-2 · stepper de impostores en oscuro | Media | ✅ corregido y verificado (2026-08-28) |
| BUG-3 · ojo del naipe pequeño | Baja | ✅ corregido y verificado (2026-08-28) |
| BUG-4 · pupila en tema claro | Baja | ✅ corregido y verificado (2026-08-28) |
| BUG-5 · pie repite el nombre del jugador activo | Media | ✅ corregido y verificado (2026-08-31) |
| BUG-6 · crash por SENSOR_DELAY_FASTEST sin permiso | Crítica | ✅ corregido y verificado (2026-09-02) |
| BUG-7 · pérdida de jugadores al cambiar de idioma | Media | ✅ corregido y verificado (2026-09-11) |
| BUG-8 · «el color de la carta delataba al impostor» | — | ❌ descartado: la hipótesis era falsa (2026-10-14) |
| BUG-9 · contrastes claros por debajo de AA | Media | ✅ corregido y verificado (2026-10-14) |
| BUG-10 · la carta de rol quedó sin señal identificativa | Media | ✅ corregido y verificado (2026-10-14) |
| BUG-11 · girar el móvil reiniciaba la partida | Crítica | ✅ corregido y verificado en teléfono (2026-10-14); en tablet ≥600 dp rota el layout pero no pierde la partida |
