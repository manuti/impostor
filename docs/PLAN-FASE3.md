# Plan — Fase 3: mejora pre-i18n (C-1) + i18n / internacionalización (v0.4.0)

> **Estado: CERRADA (2026-09-11).** C-1 se publicó como v0.3.5 y la i18n (UI + contenido, `en-GB` / `es-ES`) se cerró como **v0.4.0**; todas las decisiones D-F3-1…D-F3-6 quedaron resueltas (§5). El orden de fases fijado el **2026-09-10** es fase 3 → fase 4.
> Incluye **C-1** (no repetir la palabra secreta entre partidas consecutivas) como **primer cambio, antes de la i18n** (§3.0).
> La §4 recoge las reglas de las fases 2 y 2.5, **vigentes** para el resto del proyecto.

---

## 1. Objetivo

Hacer la app multidioma: **inglés (`en-GB`) como default y fallback** más **español de España (`es-ES`)**; más locales si procede (los aporta la comunidad por PR, revisados por el code owner — §5/D-F3-3). Dos planos:

- **UI**: todas las cadenas de interfaz externalizadas y traducibles.
- **Contenido del juego**: categorías y las 110 palabras de `GameWords.kt` (decisión de alcance, ver §5).

Además, y **antes** de la i18n, esta fase incluye una mejora de juego independiente de la traducción:

- **C-1 · No repetir la palabra secreta entre partidas consecutivas** (§3.0), movida aquí desde el plan de la fase 4 (2026-09-10).

## 2. Estado actual (verificado 2026-09-11, al cerrar la fase 3 con v0.4.0)

- **Dos idiomas de UI**: `res/values/strings.xml` (**inglés `en-GB`, default y fallback**) y `res/values-es/strings.xml` (**español `es-ES`**), cada uno con **90 `<string>` + 2 grupos `<plurals>`** (`setup_summary_players`, `setup_summary_impostors`) y 19 cadenas con formato (`%1$s`/`%1$d`); los datos dinámicos van siempre por placeholders, sin concatenación ni pluralización manual en las pantallas. El `values-en/` intermedio se eliminó (el default ya es inglés).
- **UI 100 % externalizada**: todos los textos visibles salen de recursos (`stringResource(...)` / `pluralStringResource(...)`); referencias a `R.string.*` por pantalla (grep 2026-09-11): SetupScreen 22 (+2 `R.plurals.*`), RoleReveal 28, GameScreen 28, EndGameScreen 9, App 1, ThemeToggle 2 y LanguageButton 3. **No quedan cadenas de UI hardcodeadas en Kotlin** (el grep solo devuelve glifos, números, emojis, prefijos decorativos y claves de prefs).
- **Selector de idioma in-app** (`ui/components/LanguageButton.kt`, junto al botón de tema en todas las pantallas): `AppCompatDelegate.setApplicationLocales` con `AppLocalesMetadataHolderService` y `autoStoreLocales=true` (la elección persiste sin código propio); `res/xml/locales_config.xml` declara `en-GB` y `es-ES` y el manifest lleva `android:localeConfig`. Implicó `MainActivity` → `AppCompatActivity` con `Theme.AppCompat.NoActionBar` y la dependencia `androidx.appcompat:appcompat:1.7.0` (D-F3-2: A + B).
- **Aviso al cambiar de idioma** (BUG-7, detectado en la prueba en dispositivo): cambiar de locale **recrea la actividad** y **pierde el estado en memoria** (jugadores y configuración). `LanguageButton(hasPlayers)` pide confirmación con `language_change_title`/`language_change_text`/`language_change_confirm` si ya hay jugadores; sin jugadores aplica directo. Corregido y verificado el 2026-09-11 (ver `docs/BUGS.md`).
- Restos NO traducibles (glifos/números/claves): `Text("−")`/`Text("+")` (SetupScreen), conteos numéricos, emojis, prefijos decorativos `"✔ "`/`"• "` (RoleRevealScreen) y claves de prefs (`impostor_prefs`, `dark_theme`). El join de la configuración (`"$playersSummary, $impostorsSummary"`) se resolvió con un recurso de dos placeholders (regla 7).
- **Contenido del juego localizado y editable (JSON, no Kotlin)**: `app/src/main/assets/words/es-ES.json` y `en-GB.json`, **11 categorías × 10 palabras = 110 `{word, hint}` por idioma**, con **id estable** de categoría (`animals`, `food`, `places`, `objects`, `professions`, `sports`, `countries`, `nature`, `concepts`, `fantasy`, `movies`) separado del **nombre mostrado** (traducido). La pseudo-categoría «Todas» es `WordRepository.ALL_ID = "all"`, generada en código. `game/GameWords.kt` se eliminó al quedarse sin referencias (D-F3-1: traducir todo, hecho).
- **`game/WordRepository.kt`**: carga los assets con `org.json` (sin dependencias) y cachea por locale; `es*` → es-ES, cualquier otro → **en-GB (fallback inglés)**. `randomWord(context, categoryId, exclude)` conserva la semántica de C-1 (`candidates.ifEmpty { words }`).
- **Validación del contenido**: `tools/validate_words.py` (solo librería estándar) comprueba esquema, coherencia `locale` ↔ nombre de fichero, ids únicos y normalizados, campos no vacíos y duplicados, y coherencia entre idiomas (mismos ids, orden y nº de palabras); sin argumentos localiza los assets desde su propia ruta (apto para CI). Salida verificada: `Contenido válido.` Es la primera barrera automática de los PRs de idioma (§5/D-F3-3).
- **C-1 (v0.3.5, publicada)**: `getRandomWord(category, exclude)` en `GameWords.kt` evita repetir la última palabra si el pool conserva más de una opción; `App.kt` memoriza `lastWord` en estado de sesión. Probado en dispositivo, commiteado (`de71c30`) y publicado el 2026-09-10 (release v0.3.5 con `impostor-v0.3.5.apk`).
- **Terminología y ortografía (es-ES)**: unificado «Naipe» → **«Carta»** en las cadenas (`role_card_locked_cd`, `role_card_revealed_cd`, `help_line2`) y en la documentación; se mantiene la regla de «píldora» (nunca «pillula»). El español de referencia pasa a ser **es-ES** (antes es-419).
- **RTL**: el manifest declara `android:supportsRtl="true"` y no hay `left/right` en Kotlin (se usa `start/end`), pero **no se soporta ningún locale RTL** ni se ha verificado el layout en árabe/hebreo (D-F3-4: pospuesto).
- Versión al cerrar la fase: **0.4.0** (`versionCode = 9`); release con APK `impostor-v0.4.0.apk`. Historial de releases: v0.1.0…v0.4.0 (v0.3.5 fue el patch pre-i18n de C-1, 2026-09-10).
- Aviso de la fase 2 (histórico): antes de la fase 2 (2026-08-26) `strings.xml` solo tenía `app_name`, 0 usos de `R.string` y ~62 cadenas hardcodeadas; las fases 2, 2.5 y 3 cumplieron las reglas de la §4.

## 3. Trabajo de la fase 3 (realizado)

### 3.0 · C-1 · No repetir la palabra secreta entre partidas consecutivas (primer cambio, pre-i18n)

> Movido aquí desde `PLAN-FASE4.md` §6 el **2026-09-10**: es una mejora de juego, no un requisito de publicación en Play, y se implementa antes de la i18n.

- **Qué**: al empezar una partida nueva, evitar que la palabra secreta coincida con la de la partida anterior. Hoy `getRandomWord` usa `pool.random()` sin memoria (`GameWords.kt`): con una categoría de 10 palabras, repetir la anterior tiene 1/10 de probabilidad; en varias partidas seguidas es muy probable.
- **Cómo**: recordar la última palabra elegida y excluirla del pool en la siguiente selección (si el pool tiene más de 1 palabra; si solo queda 1, permitir repetir). Basta en memoria por sesión (`App.kt`, en `startGame`); opcional: persistirla en SharedPreferences para que sobreviva al cierre de la app.
- **Toca**: `game/GameWords.kt` (nueva función de selección con exclusión o parámetro) y `ui/App.kt` (pasar/excluir la última palabra). No cambia la mecánica de juego ni añade dependencias.
- **Verificación**: jugar dos partidas seguidas con la misma categoría → la palabra no se repite; pool de 1 palabra → permite repetir; sin regresión en el flujo normal.
- **Release**: **decidido (2026-09-10): patch propio v0.3.5** (`versionCode` 7 → 8), con su release tras la prueba en dispositivo y **antes** de empezar la i18n (patrón de la fase 2.5). **Hecho (2026-09-11): probado en dispositivo y publicado** (release v0.3.5 con APK `impostor-v0.3.5.apk`, commit `de71c30`).

Y después, el trabajo de i18n propiamente dicho, **completado el 2026-09-11**:

1. ✅ **Externalización**: al cerrar la fase 2.5 la UI ya estaba 100 % externalizada; solo quedaba un join en `SetupScreen.kt`, resuelto con un recurso de dos placeholders.
2. ✅ **Locales**: **inglés (`en-GB`) como default y fallback** en `res/values/` y **español (`es-ES`)** en `res/values-es/`; el `values-en/` intermedio se eliminó al pasar el inglés a default.
3. ✅ **[DECIDIDO 2026-09-11] Idioma (D-F3-2): A + B** — la app sigue el **idioma del sistema** (`android:localeConfig`) y además ofrece un **selector in-app** (`LanguageButton`) para cambiarlo solo para la app. Se deja como trabajo futuro ampliar B (p. ej. idioma de la UI distinto del de las palabras), posiblemente en una fase previa a la 4.
4. ✅ **Contenido traducido (D-F3-1)**: palabras, pistas y categorías por locale en `app/src/main/assets/words/*.json`. La consecuencia de diseño se resolvió **separando el id estable de categoría del nombre mostrado** (`GameConfig.category` = id; `WordPair.category` = nombre localizado).
5. ✅ **`<plurals>` y placeholders**: conteos con `setup_summary_players` / `setup_summary_impostors` y 19 cadenas con formato; sin concatenación de texto.
6. ⏸️ **RTL (D-F3-4)**: se usa `start/end`, pero no hay locales RTL ni verificación de layout. **Pospuesto** hasta que haya un candidato real (no se plantean idiomas que compliquen el proyecto).
7. ✅ **Tipografías**: los alfabetos de `en` y `es` están cubiertos por las fuentes del sistema.
8. ⏸️ **TTS**: no implementada; sigue reservada a textos **no secretos** (nunca palabra/pista). La revisión de esa regla es la tarea T-1 de `PLAN-FASE4.md` §7, post-publicación.
9. ✅ **Publicación**: **v0.4.0** (`versionCode` 8 → 9), release con APK `impostor-v0.4.0.apk`, README y planes actualizados.

## 4. Reglas vinculantes para las fases 2 y 2.5 (no complicar la i18n futura)

1. **Todo texto nuevo o reescrito de UI va a recursos de cadena** (`res/values/strings.xml`, **inglés `en-GB` como default y fallback**; `res/values-es/strings.xml` para español) y se usa con `stringResource(R.string.x)`. Prohibido añadir strings hardcodeados nuevos en Kotlin.
2. **Nada de concatenación de texto**: usar placeholders (`"Jugador %1$d de %2$d"`, `"Pista: %1$s"`). Los datos dinámicos (nombres, conteos, categoría, pista) siempre como argumentos.
3. **`<plurals>`** para cualquier conteo (nº de jugadores, impostores, vivos, eliminados) en vez de pluralización manual.
4. **Texto en sp**, nunca en dp; usar `start/end` (no `left/right`) en paddings y alineaciones.
5. **No meter gramática española en la lógica**: las frases completas viven en recursos; la lógica solo combina datos.
6. **Contenido del juego separado de la UI**: palabras y categorías viven en su propio modelo de datos (`game/WordRepository.kt` + `app/src/main/assets/words/<locale>.json`), nunca acopladas a cadenas de UI ni a lógica de pantalla. El **id de categoría es estable** (datos y filtro) y el **nombre mostrado es traducible**: no confundirlos.
7. No añadir dependencias de texto/formato que asuman español (p. ej. no formatear listas uniendo con ", " en código; usar recursos).
8. **Ortografía y terminología correctas en cadenas y documentación** (es-ES para español, en-GB para inglés): usar siempre “píldora” (con tilde), nunca “pillula” (acordado 2026-09-02 tras corregirlo en la v0.3.3), y revisar tilde y grafía de cualquier término nuevo. **Actualización 2026-09-11**: el español de referencia es **es-ES** (no es-419) y la terminología se unificó a **“carta”** (antes “naipe”).

## 5. Decisiones de la fase 3 [RESUELTAS]

- D-F3-1: ¿Traducir también el contenido (110 palabras + categorías) o solo la UI? → **DECIDIDO (2026-09-10): traducir todo** (UI + contenido: palabras, pistas y categorías). **Hecho** (assets JSON por locale).
- D-F3-2: ¿Idioma por sistema o selector in-app? → **DECIDIDO (2026-09-11): A + B** — se sigue el **idioma del sistema** y además hay **selector in-app**. Ampliar B (separar idioma de UI y de contenido, etc.) queda para una fase futura, posiblemente previa a la 4.
- D-F3-3: Locales iniciales (¿solo en, o también pt, fr…?). → **DECIDIDO (2026-09-11): `es` y `en`**, implementados como **es-ES** y **en-GB** (el usuario pidió «en-UK»; el tag BCP-47 correcto es **`en-GB`**, y «UK» es el país, no la variante lingüística). Las listas de palabras viven en **JSON editable por cualquiera desde GitHub** (`assets/words/*.json`) y los PRs de idioma se validan con `tools/validate_words.py` y los revisa el **code owner** (configurado en `.github/`).
- D-F3-4: ¿Soporte RTL desde el inicio? → **DECIDIDO (2026-09-11): pospuesto** hasta que haya un candidato real; no se plantean idiomas que compliquen el proyecto.
- D-F3-5: ¿C-1 se publica como patch propio (v0.3.5) o se acumula hasta el cierre v0.4.0 de la i18n? → **DECIDIDO (2026-09-10): patch propio v0.3.5** (publicado 2026-09-10; hecho).
- D-F3-6 (nueva, 2026-09-11): ¿qué hacer al cambiar de idioma si ya hay jugadores añadidos? → **DECIDIDO: avisar y pedir confirmación** antes de aplicar el cambio, porque el cambio de locale recrea la actividad y perdería los nombres (BUG-7). Alternativa futura: `rememberSaveable`/persistencia para no perder el borrador.

## 6. Relación con otras fases

- **Cambio de alcance (2026-09-10)**: C-1 se movió desde el plan de la fase 4 a esta fase 3 (mejora pre-i18n, §3.0); la fase 4 arranca **sin C-1**. Orden de fases: **3 → 4**.
- La fase 2 (usabilidad, v0.3.0) deja la infraestructura de strings lista y aplica estas reglas.
- La **fase 2.5** (mejoras intermedias, `PLAN-FASE2.5.md`) también aplica estas reglas; sus cambios no deben acoplar contenido a cadenas.
- La fase 3 (i18n) queda cerrada con: UI en dos idiomas, selector in-app con aviso, contenido localizado en JSON, validación automática de PRs de idioma y v0.4.0 publicada.
- La TTS (descartada para palabra/pista en esta fase) podría reaparecer solo como lectura de instrucciones/tutorial localizadas, nunca sobre contenido secreto — salvo el modo privado de la tarea T-1 (`PLAN-FASE4.md` §7, post-publicación), que revisa esa regla al implementarla.
- **Fase 4**: arranca sin C-1 y con la i18n ya cerrada; las lecciones de la fase 3 se añadieron a `PLAN-FASE4.md` §3.1.
