# Plan — Fase 3: i18n / internacionalización (v0.4.0)

> **Estado: propuesta para una fase FUTURA. No implementar ahora.**
> La §4 (reglas para las fases 2 y 2.5) **es vinculante**: las fases 2 y 2.5 deben respetarla para no complicar la i18n posterior.

---

## 1. Objetivo

Hacer la app multidioma: español (es-419, default) + inglés como primer idioma añadido; más locales si procede. Dos planos:

- **UI**: todas las cadenas de interfaz externalizadas y traducibles.
- **Contenido del juego**: categorías y las 110 palabras de `GameWords.kt` (decisión de alcance, ver §5).

## 2. Estado actual (verificado 2026-08-26)

- `res/values/strings.xml` existe pero **solo contiene `app_name`**; el código Kotlin tiene **0 usos** de `stringResource`/`R.string`.
- **~62 cadenas de UI** hardcodeadas en español repartidas así:
  - `GameScreen.kt`: 22
  - `SetupScreen.kt`: 19
  - `RoleRevealScreen.kt`: 10
  - `EndGameScreen.kt`: 9
  - `App.kt`: 2
- **113 cadenas de contenido** (palabras + categorías) en `game/GameWords.kt`.
- Patrones actuales que complican i18n y que la fase 2 debe eliminar:
  - **Concatenación dentro de literales**: `"Categoría: ${currentWord.category}"`, `"Pista: ${currentWord.hint}"`, `"Jugador ${currentPlayerIndex + 1} de ${players.size}"`.
  - **Pluralización manual**: `"impostor${if (shownImpostors > 1) "es" else ""}"` (caso clásico de `<plurals>`).

## 3. Trabajo previsto para la fase 3

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

## 6. Relación con otras fases

- La fase 2 (usabilidad, v0.3.0) deja la infraestructura de strings lista y aplica estas reglas.
- La **fase 2.5** (mejoras intermedias, `PLAN-FASE2.5.md`) también aplica estas reglas; sus cambios no deben acoplar contenido a cadenas.
- La fase 3 (i18n) es mayoritariamente "rellenar traducciones + selector de idioma + contenido localizado" (su versión exacta depende de la decisión de versionado de la 2.5).
- La TTS (descartada para palabra/pista) podría reaparecer solo como lectura de instrucciones/tutorial localizadas, nunca sobre contenido secreto.
