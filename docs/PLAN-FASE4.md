# Plan de la fase 4 — Publicación en Google Play

> Documento de arranque de la fase 4 (publicación en Google Play), creado el **2026-09-02** al cerrar la fase 2.5, con las lecciones aprendidas en las fases 2, 2.5 y 3.
> La fase 4 **no ha empezado**: este documento fija el punto de partida, las lecciones y el checklist de la tienda.
> **Orden de fases (2026-09-10)**: primero la fase 3 (C-1 pre-i18n + i18n v0.4.0, ver `PLAN-FASE3.md`), **cerrada el 2026-09-11 con v0.4.0**; esta fase 4 va después. **C-1 ya no pertenece a esta fase.**

---

## 1. Estado actual al inicio de la fase 4

- App publicada hasta **v0.4.0** (release en GitHub con APK `impostor-v0.4.0.apk`). Rama `main`, historial limpio.
- Fases cerradas: 1 (MVP v0.1.0/v0.2.0), 2 (usabilidad v0.3.0), 2.5 (mejoras intermedias v0.3.1–v0.3.4) y 3 (C-1 v0.3.5 + i18n v0.4.0).
- **i18n cerrada (fase 3)**: UI en **en-GB** (default y fallback) y **es-ES**, selector de idioma in-app con aviso, contenido del juego en `app/src/main/assets/words/*.json` (editable por la comunidad desde GitHub) y `tools/validate_words.py` + CODEOWNERS como control de los PRs de idioma.
- **Permisos declarados: ninguno** en el manifest. Es un estado ideal para Play Store — mantenerlo mientras sea posible.
- Licencia: CC BY-NC 4.0. La app se distribuiría gratuita (uso no comercial) — coherente con la licencia; revisar los términos de Play sobre contenido gratuito al publicar.
- Repo público: `manuti/impostor` (code owner: `manuti`). Release actual: v0.4.0.
- **Orden de fases**: la fase 3 (mejora pre-i18n C-1 + i18n v0.4.0) se ejecuta **antes** que esta fase 4 (decisión 2026-09-10).

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

## 3.1 Lecciones de la fase 3 (i18n, v0.3.5–v0.4.0)

1. **Selector in-app con la vía soportada**: `AppCompatDelegate.setApplicationLocales(...)` exige `AppCompatActivity` + el servicio `AppLocalesMetadataHolderService` con `autoStoreLocales="true"` (persiste la elección sin código propio), `res/xml/locales_config.xml` y `android:localeConfig` en el manifest. No improvisar un override manual de `Configuration`.
2. **Cambiar de idioma recrea la actividad** y por tanto **pierde el estado en memoria** (BUG-7: se perdían los jugadores añadidos). Si hay datos que perder, **avisar y confirmar** antes de aplicar; la alternativa sin pérdida es `rememberSaveable` o persistir el borrador de la partida.
3. **Default y fallback viven en `res/values/`**: con el **inglés** ahí, cualquier locale sin traducir cae en inglés. No hace falta `values-en/` (duplicaría el default) — el español va en `res/values-es/`.
4. **El contenido del juego es datos, no UI**: JSON en `assets/words/<locale>.json`, cargado con `org.json` (sin dependencias) y cacheado por locale.
5. **Id estable ≠ nombre mostrado**: la categoría era a la vez clave de filtro y texto en español; al traducir hay que separarlas o se rompen el filtro y la categoría guardada.
6. **Verificar los assets dentro del APK**, no solo en el repo: un JSON mal empaquetado no rompe la compilación pero sí el arranque. `python3 -m zipfile -l "$APK" | grep assets/words`.
7. **Validación automática + revisión humana**: `tools/validate_words.py` (esquema, ids, coherencia entre idiomas) es la primera barrera; el workflow de GitHub lo ejecuta en cada PR que toque `assets/words/` y CODEOWNERS exige la revisión del propietario. Ningún idioma nuevo entra sin revisión humana.
8. **`en-UK` no existe**: el tag BCP-47 del inglés británico es **`en-GB`** (y `UK` es el país, no la variante lingüística); el español de España es `es-ES`. Usar siempre el tag correcto en nombres de fichero, `localeConfig` y documentación.
9. **Ortografía y terminología como requisito de producto**: unificar términos es-ES («Naipe» → «Carta») y revisar tildes («píldora»). Las traducciones automáticas sin revisar no se aceptan.
10. **No delegar en subagentes trabajo que exija confirmaciones del usuario**: el flujo de aprobación se rompe (el usuario no puede atender a los subagentes). Ejecutar ese trabajo en primer plano, desde el workspace principal.

## 4. Reglas vinculantes heredadas (PLAN-FASE2.5 §5)

- Modelos como datos puros; acciones como funciones puras sobre `GameState`.
- Sensores/periféricos aislados en componentes reutilizables con ciclo de vida gestionado (`DisposableEffect`), sin lógica de negocio dentro (ya cumplido con `MotionDetector`).
- **No añadir analytics/telemetría en esta fase** (telemetrydeck se evalúa en fase 4, con su política de consentimiento y declaración de privacidad).
- Cambios pequeños; compilar y probar en dispositivo tras cada uno. La UI está internacionalizada desde la fase 3 (v0.4.0): **cualquier texto nuevo debe entrar al menos en `res/values/` y `res/values-es/`** (y en los locales que se añadan); el contenido nuevo de juego, en los JSON de idioma.

## 5. Checklist de requisitos de Google Play (a completar en la fase 4)

- [ ] **Cuenta de desarrollador** de Google Play (pago único; revisar tarifa vigente) y acceso a Play Console.
- [ ] **Manifest/permisos**: mantener **cero permisos** (estado actual). Si se añade alguno, justificarlo en la ficha (declaración de permisos de Play).
- [ ] **targetSdk 36** (ya configurado) — comprobar el requisito mínimo vigente de Play en el momento de publicar.
- [ ] **App Bundle (AAB)** para producción (`bundleRelease`), no APK; firmado con keystore de release (crear y custodiar; no versionar).
- [ ] **Icono adaptable** y recursos de ficha: capturas de pantalla (mín. 2 teléfono), gráfica destacada, título y descripciones corta/larga en **los idiomas soportados (es-ES y en-GB)** (mismas reglas de estilo que la app: ortografía correcta, p. ej. **píldora**, no *pillula*).
- [ ] **Clasificación de contenido** (cuestionario IARC) y confirmación de políticas (contenido, spam, datos).
- [ ] **Política de privacidad**: la app no recoge datos (todo local). Publicar URL de política aunque no haya recogida, según los requisitos vigentes.
- [ ] **Testing**: subir primero a *internal testing* con la lista de probadores; validar instalación desde Play (firma distinta a la de debug).
- [ ] **Cumplimiento de calidad**: sin crashes en el flujo completo (lección BUG-6), rendimiento, comportamiento sin red (la app es 100 % local).
- [ ] **Licencia y atribuciones**: revisar coherencia de CC BY-NC con la distribución gratuita en Play; atribuciones de la referencia React en la ficha o en "Acerca de".
- [ ] **Versionado de publicación**: mantener `versionCode` creciente; el `versionName` de Play puede seguir `v0.4.x` o saltar a `1.0.0` (decisión de la fase 4).
- [ ] **Telemetrydeck** (solo si se decide): integrar como componente aislado, con consentimiento y su declaración en la política de privacidad.

## 6. Cambios de la fase 4 (antes de publicar en Play) — a detallar

> **C-1 se movió a la fase 3** (`PLAN-FASE3.md` §3.0) el **2026-09-10**: es una mejora de juego, no un requisito de publicación, y se implementa antes de la i18n. Esta fase arranca sin C-1.

Trabajo previsto (a detallar al llegar; el primer cambio, por definir):
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
- **i18n (fase 3, cerrada)**: la voz debe seguir el **locale activo** y usar las cadenas ya traducidas (`res/values/` en-GB, `res/values-es/` es-ES) con placeholders (p. ej. "%1$s, tu palabra es %2$s"); el contenido sale de `WordRepository` (`assets/words/<locale>.json`), nunca de texto incrustado en Kotlin.
- **Estado de la regla (2026-09-11)**: la fase 3 cerró **sin TTS** y sin leer nunca palabra ni pista; la regla «TTS nunca contenido secreto» sigue **vigente** y solo se revisará al implementar T-1, con la condición (proximidad activa) como única vía de lectura del secreto.
- **Verificación futura en dispositivo**: al acercar el móvil a la oreja con la carta revelada se oye el rol; al retirarlo se silencia; los demás jugadores no oyen nada; el revelado visual actual no cambia.
- **Dependencias**: fase 3 (i18n) cerrada, para que la voz y las cadenas estén localizadas.
