# Contexto — Fase 2: usabilidad para personas mayores

> Documento de arranque de sesión. Léelo completo antes de tocar código.
> Creado: 2026-08-26 · Actualizado: 2026-08-26 (cambios v0.1 → v0.2) · Proyecto: El Impostor (Android nativo) · Repo: https://github.com/manuti/impostor

---

## 1. Objetivo de la fase 2

Mejorar la **usabilidad para personas mayores** de la app *El Impostor* (v0.2.0 publicada).
Las mejoras se diseñan sobre la base actual, sin cambiar la mecánica del juego (reglas en `docs/investigacion.md` §1).

**Fuera de alcance (no hacer):**
- ❌ **Versión online / multijugador a distancia** — excluida explícitamente de esta fase.
- ❌ iOS / PWA — solo Android nativo por ahora.

## 2. Estado actual del proyecto

- **App funcional v0.2.0** (probada en dispositivo por el usuario): juego del impostor local *pass & play*.
- **Publicada**: repo público `manuti/impostor`, releases `v0.1.0` y `v0.2.0` con APK adjunto (`el-impostor-v0.1.0.apk`, `el-impostor-v0.2.0.apk`).
- **Stack**: Kotlin + Jetpack Compose (Material 3), AGP 9.3.0 con Kotlin integrado (built-in), Gradle 9.7.1 (wrapper), version catalog en `gradle/libs.versions.toml`, compileSdk 36 / minSdk 24.
- **Git**: rama `main`, 4 commits (v0.1.0 → `f64bd67`, fix categorías v0.2.0 → `b24bb59`, licencia+docs → `65a6901`, contexto+gitignore); los mockups son locales e ignorados vía `.gitignore` (ver §7).
- **Licencia**: **CC BY-NC 4.0** (Creative Commons Atribución-NoComercial 4.0 Internacional) — uso educativo y derivaciones permitidas con atribución; uso comercial prohibido. Archivo `LICENSE` con el texto legal completo (fuente SPDX).

### Cambios v0.1.0 → v0.2.0

- **Fix del selector de categorías** (`SetupScreen.kt`): en v0.1.0 el menú no se abría; en v0.2.0 se migró a `ExposedDropdownMenuBox` de Material 3 (detalle en §5).
- **Versión**: `versionName` 0.1.0 → 0.2.0 · `versionCode` 1 → 2.
- **Probado en dispositivo por el usuario (2026-08-26)**: las categorías se seleccionan y las palabras se filtran correctamente.

## 3. Cómo compilar, probar e instalar

```bash
# Termux (este dispositivo)
cd /data/data/com.termux/files/home/impostor
sh gradlew :app:assembleDebug          # APK → app/build/outputs/apk/debug/app-debug.apk
cp app/build/outputs/apk/debug/app-debug.apk ~/storage/downloads/   # instalarlo desde Descargas
```

- Compilar con `./gradlew` en Linux/macOS/Windows (requiere JDK 17+ y Android SDK).
- **Cada cambio de UI debe probarse en el dispositivo real** (emulador no disponible aquí).
- La primera ejecución de `sh gradlew` descarga la distribución de Gradle 9.7.1 (red + unos minutos).

## 4. Arquitectura del código (dónde está cada cosa)

```
app/src/main/kotlin/com/impostor/game/
├── MainActivity.kt          # Entrada; setContent { ImpostorTheme { App() } }
├── ui/App.kt                # Máquina de estados: SETUP → ROLE_REVEAL → PLAYING → ENDED
│                            #   · startGame: barajado Fisher-Yates para impostores, getRandomWord()
│                            #   · eliminatePlayer: condición de victoria (impostores ≥ civiles → ganan)
│                            #   · resetGame: vuelve a SETUP
├── ui/screens/
│   ├── SetupScreen.kt       # Jugadores, nº impostores, pista, CATEGORÍA (ExposedDropdownMenuBox, corregido en v0.2.0)
│   ├── RoleRevealScreen.kt  # "Mantén pulsado" para ver rol en privado (pointerInput + awaitFirstDown)
│   ├── GameScreen.kt        # Debate: botones de jugador vivos/eliminados, confirmación de voto
│   └── EndGameScreen.kt     # Victoria, palabra secreta, roles revelados, "Jugar de Nuevo"
├── ui/theme/Theme.kt        # Tema oscuro Material 3 (colores hardcodeados)
└── game/
    ├── GameModels.kt        # GamePhase, Role, Player, WordPair, GameConfig, GameState
    └── GameWords.kt         # wordList (110 palabras, 11 categorías), getAllCategories(), getRandomWord()
```

Notas:
- No hay ViewModel ni navegación; todo el estado vive en `App.kt` (válido para MVP, pero la fase 2 añadirá ajustes de UI).
- Las pantallas usan `dp` fijos (ej. botones de 56.dp) y colores literales (`0xFFF87171`, etc.) — relevantes para los cambios de accesibilidad.

## 5. BUG CORREGIDO en v0.2.0: el selector de categorías

**Síntoma (v0.1.0):** las categorías de palabras no funcionaban (el menú no se abría).

**Diagnóstico (confirmado):**
- La lógica de filtrado era correcta: `getRandomWord(category)` filtra `wordList` por `it.category == category`, y los nombres de categoría del menú coinciden exactamente con los de `wordList` (`getAllCategories()`).
- El fallo estaba en `SetupScreen.kt`: se usaba el patrón
  `OutlinedTextField(readOnly = true, modifier = Modifier.clickable { categoryMenuOpen = true })`.
  **Ese patrón no funciona en Compose**: el `TextField` consume el gesto de toque y el `clickable` externo no llega a dispararse (fallo conocido: stackoverflow.com/q/67902919, JetBrains/compose-multiplatform#220).

**Fix aplicado en v0.2.0 (commit `b24bb59`):**
- Migrado al patrón oficial de Material 3: `ExposedDropdownMenuBox` + `ExposedDropdownMenu` (es **miembro del scope** `ExposedDropdownMenuBoxScope`, no es importable como función top-level) + `menuAnchor(MenuAnchorType.PrimaryNotEditable)` + `ExposedDropdownMenuDefaults.TrailingIcon`.
- Requiere `@OptIn(ExperimentalMaterial3Api::class)` (API experimental en material3 1.3.0).
- **Verificado en dispositivo por el usuario (2026-08-26): funciona.**

**Lección para la fase 2 (accesibilidad):** `ExposedDropdownMenuBox` es además el patrón recomendado para selectores accesibles (TalkBack anuncia el estado expandido/colapsado) — usarlo para cualquier selector nuevo.

## 6. Puntos de partida para la usabilidad (fase 2)

Base: `docs/investigacion.md` §6 (8 puntos detectados al comparar las versiones existentes). Pendiente de concretar la lista definitiva con el usuario:

1. **Texto grande y legible**: tipografía mayor y configurable; evitar tamaños < 16sp en contenido.
2. **Alto contraste**: revisar colores literales (rojo/azul/verde sobre fondo oscuro) contra WCAG AA; opción de tema claro.
3. **Objetivos táctiles grandes**: botones ≥ 56dp ya en parte; ampliar zonas táctiles de filas, iconos y chips.
4. **Sin gestos complejos**: "mantén pulsado" para revelar rol puede ser difícil (tremor) → alternativa con botón grande "Toca para revelar" y más tiempo de lectura; ampliar el hold de 300 ms.
5. **Tiempos ampliados / sin prisa**: quitar o relajar límites de tiempo; no hay timer en la v0.1 (bien), mantenerlo así o hacerlo opcional.
6. **TTS (lectura en voz alta)**: botón para que la app lea la palabra/pista al jugador (android.speech.tts.TextToSpeech).
7. **Lenguaje sencillo + iconos grandes**: sustituir jerga ("blufear", "pass & play"); instrucciones claras paso a paso en cada pantalla.
8. **Seguridad y recuperación**: confirmaciones claras antes de acciones destructivas (ya existe en votación/salir); mantener "deshacer" donde aplique.
9. **Tutorial guiado**: primera partida con explicación por pasos (o pantalla de "Cómo se juega" con imágenes).
10. **Persistencia**: recordar jugadores y categoría de la última partida (ya hay `savedPlayerNames`/`savedCategory` en App.kt).

**Checklist de verificación accesibilidad (probar en dispositivo):**
- [ ] TalkBack activado: toda la app navegable, contentDescription en iconos, orden lógico.
- [ ] Escala de fuente del sistema al 200%: nada se corta ni se solapa.
- [ ] Contraste: leer textos sobre fondos de color en estado normal y pulsado.
- [ ] Tamaño táctil: todos los objetivos ≥ 48dp (ideal 56dp).
- [ ] Prueba con una persona mayor real si es posible (objetivo final).

## 7. Mockups disponibles (sin commitear, en la raíz del proyecto)

El usuario generó mockups de UI con diferentes herramientas — **revisarlos antes de rediseñar**:

- HTML interactivo: `mockup-Claude.html`, `mockup-DeepSeek.html` (abrir con navegador o leer el código)
- Imágenes: `mockup-chaggpt.png`, `mockup-gemini.png`
- Descripciones/texto: `mockup-claude.md`, `mockup-DeepSeek.md`, `mockup-chatgpt.md`, `mockup-gemini.md`

Están **ignorados en git** (`.gitignore` → `mockup-*`): son solo para trabajo local y **no se suben al repo**. No incorporarlos salvo decisión contraria del usuario. Tomar uno o varios como referencia visual para la fase 2.

## 8. Referencias útiles

- `docs/investigacion.md` — reglas del juego, comparativa de repos, sección 6 con mejoras de usabilidad.
- `README.md` — instrucciones de build e info general.
- `referencias/Impostor-juego/` — clon local de la referencia React (no se sube a git; solo estudio).
- Repos de referencia para UX/accesibilidad vistos en la investigación: `Adolfo-GM/impostor` (hold-to-reveal), `find-the-impostor` (i18n, UI cuidada), `imposter-party-game-IOS-` (estructura por pantallas).

## 9. Convenciones y calidad

- **No tocar la mecánica del juego** salvo aprobación explícita del usuario (las reglas están bien).
- Cambios pequeños e incrementales; compilar y probar en dispositivo tras cada cambio.
- Mantener el idioma de la UI en español (es-419).
- Al terminar la fase 2: actualizar README, versionar a v0.3.0 (incrementar `versionCode`), publicar release con el APK nuevo y commitear.
