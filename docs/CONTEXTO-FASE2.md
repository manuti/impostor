# Contexto — Fase 2: usabilidad para personas mayores

> Documento de arranque de sesión. Léelo completo antes de tocar código.
> Creado: 2026-08-26 · Proyecto: El Impostor (Android nativo) · Repo: https://github.com/manuti/impostor

---

## 1. Objetivo de la fase 2

Mejorar la **usabilidad para personas mayores** de la app *El Impostor* (v0.1.0 ya funcional y publicada).
Las mejoras se diseñan sobre la base actual, sin cambiar la mecánica del juego (reglas en `docs/investigacion.md` §1).

**Fuera de alcance (no hacer):**
- ❌ **Versión online / multijugador a distancia** — excluida explícitamente de esta fase.
- ❌ iOS / PWA — solo Android nativo por ahora.

## 2. Estado actual del proyecto

- **App funcional v0.1.0** (probada en dispositivo por el usuario): juego del impostor local *pass & play*.
- **Publicada**: repo público `manuti/impostor`, release `v0.1.0` con APK adjunto (`el-impostor-v0.1.0.apk`).
- **Stack**: Kotlin + Jetpack Compose (Material 3), AGP 9.3.0 con Kotlin integrado (built-in), Gradle 9.7.1 (wrapper), version catalog en `gradle/libs.versions.toml`, compileSdk 36 / minSdk 24.
- **Git**: rama `main`, 1 commit (`f64bd67`), working tree limpio salvo 4 archivos sin trackear (ver §7).
- **Licencia**: pendiente de decidir (README lo indica; por defecto "todos los derechos reservados").

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
│   ├── SetupScreen.kt       # Jugadores, nº impostores, pista, CATEGORÍA (selector con bug, ver §5)
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

## 5. BUG CONOCIDO: el selector de categorías no funciona (corregir en esta fase)

**Síntoma reportado por el usuario (v0.1.0):** las categorías de palabras no funcionan.

**Diagnóstico (revisado en código):**
- La lógica de filtrado es correcta: `getRandomWord(category)` filtra `wordList` por `it.category == category`, y los nombres de categoría del menú son exactamente los de `wordList` (`getAllCategories()`).
- El problema está en `SetupScreen.kt` (~línea 213): se usa el patrón
  `OutlinedTextField(readOnly = true, modifier = Modifier.clickable { categoryMenuOpen = true })`.
  **Este patrón no funciona en Compose**: el `TextField` consume el gesto de toque y el `clickable` externo no llega a dispararse (fallo conocido: stackoverflow.com/q/67902919, JetBrains/compose-multiplatform#220). Resultado: tocar el campo no abre el menú → el usuario no puede cambiar de categoría.

**Fix propuesto (a implementar y verificar en dispositivo):**
- Usar el patrón oficial de Material 3: `ExposedDropdownMenuBox` + `ExposedDropdownMenu` (además mejora la accesibilidad del selector, alineado con la fase 2).
- Alternativa rápida si el anterior diera problemas: Box envolvente con un overlay `clickable` transparente encima del TextField.

**Verificación:** seleccionar cada categoría → la palabra revelada debe pertenecer a esa categoría (el impostor ve la pista, no la palabra; la categoría se muestra en GameScreen y EndGameScreen).

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

El usuario generó 4 mockups de UI con diferentes herramientas — **revisarlos antes de rediseñar**:

- `mockup-Claude.html` y `mockup-DeepSeek.html` (HTML interactivo — abrir con navegador o leer el código)
- `mockup-chaggpt.png` y `mockup-gemini.png` (imágenes)

Están sin trackear en git (`git status` los muestra como `??`). Decidir con el usuario si se incorporan al repo (p. ej. `docs/mockups/`) y cuál se toma como referencia visual para la fase 2.

## 8. Referencias útiles

- `docs/investigacion.md` — reglas del juego, comparativa de repos, sección 6 con mejoras de usabilidad.
- `README.md` — instrucciones de build e info general.
- `referencias/Impostor-juego/` — clon local de la referencia React (no se sube a git; solo estudio).
- Repos de referencia para UX/accesibilidad vistos en la investigación: `Adolfo-GM/impostor` (hold-to-reveal), `find-the-impostor` (i18n, UI cuidada), `imposter-party-game-IOS-` (estructura por pantallas).

## 9. Convenciones y calidad

- **No tocar la mecánica del juego** salvo aprobación explícita del usuario (las reglas están bien).
- Cambios pequeños e incrementales; compilar y probar en dispositivo tras cada cambio.
- Mantener el idioma de la UI en español (es-419).
- Al terminar la fase 2: actualizar README, versionar (v0.2.0), publicar release con el APK nuevo y commitear (incluida la decisión de licencia si el usuario la toma).
