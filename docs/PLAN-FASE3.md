# Plan — Fase 3: mejora pre-i18n (C-1) + i18n / internacionalización (v0.4.0)

> **Estado: PRÓXIMA FASE (a arrancar).** Es la fase que sigue a la 2.5; el orden de fases fijado el **2026-09-10** es fase 3 → fase 4.
> Incluye **C-1** (no repetir la palabra secreta entre partidas consecutivas) como **primer cambio, antes de la i18n** (§3.0).
> La §4 recoge las reglas de las fases 2 y 2.5, **vigentes** para el resto del proyecto.

---

## 1. Objetivo

Hacer la app multidioma: español (es-419, default) + inglés como primer idioma añadido; más locales si procede. Dos planos:

- **UI**: todas las cadenas de interfaz externalizadas y traducibles.
- **Contenido del juego**: categorías y las 110 palabras de `GameWords.kt` (decisión de alcance, ver §5).

Además, y **antes** de la i18n, esta fase incluye una mejora de juego independiente de la traducción:

- **C-1 · No repetir la palabra secreta entre partidas consecutivas** (§3.0), movida aquí desde el plan de la fase 4 (2026-09-10).

## 2. Estado actual (verificado 2026-09-02, tras las fases 2 y 2.5)

- `res/values/strings.xml` (default, es-419): **82 `<string>` + 2 grupos `<plurals>`** (`setup_summary_players`, `setup_summary_impostors`), todos con placeholders `%1$s`/`%1$d`; sin concatenación de texto ni pluralización manual en las pantallas.
- **UI 100 % externalizada**: la UI usa `stringResource(R.string.x)` en SetupScreen (22 usos), RoleReveal (27), GameScreen (27), EndGameScreen (8), App (2) y ThemeToggle (3). **No quedan cadenas de UI en español hardcodeadas en Kotlin**.
- Restos NO traducibles (glifos/números/claves): `Text("−")`/`Text("+")` (SetupScreen), conteos numéricos, emojis, prefijos decorativos `"✔ "`/`"• "`, claves de prefs (`impostor_prefs`, `dark_theme`). Un único patrón a pulir en la fase 3 (regla 7): el join `"$playersSummary, $impostorsSummary"` (SetupScreen.kt:260) → recurso con dos placeholders `%1$s, %2$s`.
- **Contenido del juego**: `game/GameWords.kt` con **110 `WordPair`** (palabra + pista, en español) en **11 categorías** (Animales, Comida, Lugares, Objetos, Profesiones, Deportes, Países, Naturaleza, Conceptos, Fantasía, Películas) + la pseudo-categoría `"Todas"`. Separado de la UI; **sin localizar** (D-F3-1).
- **Idioma**: no hay `values-en/` ni ningún otro locale; no hay código `Locale` ni selector in-app — hoy la app solo existe en español (D-F3-2).
- **RTL**: sin pines de direccionalidad en el manifest ni `left/right` en Kotlin; la fase 2 ya usó `start/end`. Nada verificado en árabe/hebreo (D-F3-4).
- Versión actual: **0.3.4** (`versionCode = 7`); releases publicados v0.1.0…v0.3.4.
- Aviso de la fase 2 (histórico): antes de la fase 2 (2026-08-26) `strings.xml` solo tenía `app_name`, 0 usos de `R.string`, ~62 cadenas hardcodeadas y ~113 cadenas de contenido; las fases 2 y 2.5 cumplieron las reglas de la §4.

## 3. Trabajo previsto para la fase 3

### 3.0 · C-1 · No repetir la palabra secreta entre partidas consecutivas (primer cambio, pre-i18n)

> Movido aquí desde `PLAN-FASE4.md` §6 el **2026-09-10**: es una mejora de juego, no un requisito de publicación en Play, y se implementa antes de la i18n.

- **Qué**: al empezar una partida nueva, evitar que la palabra secreta coincida con la de la partida anterior. Hoy `getRandomWord` usa `pool.random()` sin memoria (`GameWords.kt`): con una categoría de 10 palabras, repetir la anterior tiene 1/10 de probabilidad; en varias partidas seguidas es muy probable.
- **Cómo**: recordar la última palabra elegida y excluirla del pool en la siguiente selección (si el pool tiene más de 1 palabra; si solo queda 1, permitir repetir). Basta en memoria por sesión (`App.kt`, en `startGame`); opcional: persistirla en SharedPreferences para que sobreviva al cierre de la app.
- **Toca**: `game/GameWords.kt` (nueva función de selección con exclusión o parámetro) y `ui/App.kt` (pasar/excluir la última palabra). No cambia la mecánica de juego ni añade dependencias.
- **Verificación**: jugar dos partidas seguidas con la misma categoría → la palabra no se repite; pool de 1 palabra → permite repetir; sin regresión en el flujo normal.
- **Release**: [DECISIÓN D-F3-5] publicar C-1 como patch propio **v0.3.5** (patrón de la fase 2.5: cada mejora con su release tras la prueba en dispositivo) o acumularlo hasta el cierre **v0.4.0** de la i18n.

Y después, el trabajo de i18n propiamente dicho:

1. Completar la externalización de cualquier cadena que quede (la fase 2 debe dejarlo casi hecho).
2. Crear `values-en/` (y los locales acordados) con las traducciones.
3. **[DECISIÓN]** Idioma: seguir el del sistema vs selector de idioma dentro de la app (para un juego pass & play que se presta, un selector in-app puede ser más cómodo; decidir).
4. Traducir el **contenido**: categorías y palabras por locale (estructura de datos localizada, no cadenas de UI).
5. `<plurals>` para conteos (jugadores, impostores); placeholders `%1$s` / `%1$d`; sin concatenación.
6. RTL: usar `start/end` en padding/alignment; verificar layout en árabe/hebreo si se soportan.
7. Tipografías que cubran los alfabetos de los locales soportados (acentos españoles ya cubiertos; cirílico, etc. si procede).
8. TTS en el idioma del locale (recordar: solo textos no secretos; nunca la palabra/pista — ver `CONTEXTO-FASE2.md` §6 punto 6).
9. Publicación: v0.4.0 (incrementar `versionCode`), release con APK, README.

## 4. Reglas vinculantes para las fases 2 y 2.5 (no complicar la i18n futura)

1. **Todo texto nuevo o reescrito de UI va a `res/values/strings.xml`** (es-419 como default) y se usa con `stringResource(R.string.x)`. Prohibido añadir strings hardcodeados nuevos en Kotlin.
2. **Nada de concatenación de texto**: usar placeholders (`"Jugador %1$d de %2$d"`, `"Pista: %1$s"`). Los datos dinámicos (nombres, conteos, categoría, pista) siempre como argumentos.
3. **`<plurals>`** para cualquier conteo (nº de jugadores, impostores, vivos, eliminados) en vez de pluralización manual.
4. **Texto en sp**, nunca en dp; usar `start/end` (no `left/right`) en paddings y alineaciones.
5. **No meter gramática española en la lógica**: las frases completas viven en recursos; la lógica solo combina datos.
6. **Contenido del juego separado de la UI**: palabras/categorías siguen en su propio modelo de datos (ya están en `GameWords.kt`), no acoplarlas a cadenas de UI ni a lógica de pantalla.
7. No añadir dependencias de texto/formato que asuman español (p. ej. no formatear listas uniendo con ", " en código; usar recursos).
8. **Ortografía es-419 correcta en cadenas y documentación**: usar siempre "píldora" (con tilde), nunca "pillula" (acordado 2026-09-02 tras corregirlo en la v0.3.3). Revisar tilde y grafía de cualquier término nuevo.

## 5. Decisiones pendientes para la fase 3 [DECISIÓN]

- D-F3-1: ¿Traducir también el contenido (110 palabras + categorías) o solo la UI?
- D-F3-2: ¿Idioma por sistema o selector in-app?
- D-F3-3: Locales iniciales (¿solo en, o también pt, fr…?).
- D-F3-4: ¿Soporte RTL desde el inicio?
- D-F3-5: ¿C-1 se publica como patch propio (v0.3.5) o se acumula hasta el cierre v0.4.0 de la i18n?

## 6. Relación con otras fases

- **Cambio de alcance (2026-09-10)**: C-1 se movió desde el plan de la fase 4 a esta fase 3 (mejora pre-i18n, §3.0); la fase 4 arranca **sin C-1**. Orden de fases: **3 → 4**.
- La fase 2 (usabilidad, v0.3.0) deja la infraestructura de strings lista y aplica estas reglas.
- La **fase 2.5** (mejoras intermedias, `PLAN-FASE2.5.md`) también aplica estas reglas; sus cambios no deben acoplar contenido a cadenas.
- La fase 3 (i18n) es mayoritariamente "rellenar traducciones + selector de idioma + contenido localizado" (su versión exacta depende de la decisión de versionado de la 2.5).
- La TTS (descartada para palabra/pista) podría reaparecer solo como lectura de instrucciones/tutorial localizadas, nunca sobre contenido secreto.
