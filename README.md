# El Impostor 🎭

Juego de deducción social **nativo para Android** (Kotlin + Jetpack Compose) en el que todos los jugadores conocen una palabra secreta excepto uno: **el impostor**, que debe fingir y adivinar la palabra sin ser descubierto.

Versión actual: **v0.4.0** (modo local *pass & play*, un solo dispositivo que se pasa de mano en mano; usabilidad mejorada para personas mayores, mejoras intermedias de la fase 2.5 y **multidioma (español e inglés) desde la fase 3**).

## Cómo se juega

1. **Configuración**: añade al menos 3 jugadores, elige nº de impostores, si el impostor recibe pista y la categoría de palabras.
2. **Reparto de roles**: cada jugador toca la carta para ver en privado su rol — gira y muestra la palabra secreta (civiles) o "ERES IMPOSTOR" con su pista (impostor).
3. **Debate**: el grupo da pistas de una palabra relacionada con la secreta, sin decirla.
4. **Votación**: todos votan quién creen que es el impostor.
5. **Victoria**: los civiles ganan si eliminan a todos los impostores; los impostores ganan si igualan o superan en número a los civiles.

> Reglas basadas en [imposter.app](https://imposter.app/es/how-to-play-imposter-game/). La investigación completa (reglas, versión online de referencia, comparativa de repos existentes y hoja de ruta) está en [`docs/investigacion.md`](docs/investigacion.md). Planes: [`docs/PLAN-FASE2.md`](docs/PLAN-FASE2.md) (usabilidad), [`docs/PLAN-FASE2.5.md`](docs/PLAN-FASE2.5.md) (mejoras intermedias), [`docs/PLAN-FASE3.md`](docs/PLAN-FASE3.md) (mejora pre-i18n + i18n) y [`docs/PLAN-FASE4.md`](docs/PLAN-FASE4.md) (publicación en Google Play).

## Características actuales (v0.4.0)

- Modo local **pass & play** (un solo dispositivo).
- **110 palabras propias** en 11 categorías (Animales, Comida, Lugares, Objetos, Profesiones, Deportes, Países, Naturaleza, Conceptos, Fantasía, Películas).
- 1–N impostores configurables; pista opcional para el impostor.
- Selector de categorías accesible (patrón `ExposedDropdownMenuBox`, v0.2.0).
- **Revelación de roles con animación de carta**: el rol se muestra al girar la carta (tap); anverso con el logo del ojo, reverso con la palabra o la pista.
- **Configuración reordenada** (v0.3.1): Título → Configuración de la partida (Categoría → Impostores → Pista) → Empezar Partida → Añadir jugadores; la **pista del impostor sale desactivada** por defecto.
- **Tema oscuro y claro** con botón **sol/luna** en la barra superior de **todas** las pantallas (v0.3.2); la preferencia se recuerda.
- **Jugador activo destacado** en la revelación (v0.3.3): píldora de contraste con su nombre y pie "Pasa el móvil a [siguiente jugador]".
- **Carta oculta al mover el dispositivo** (v0.3.4): si el móvil se agita o se gira para pasarlo a otra persona con el rol revelado, la carta se oculta al instante y se conserva el mismo turno (acelerómetro + giroscopio, sin permisos en el manifest).
- **La palabra secreta no se repite entre partidas seguidas** (v0.3.5): al empezar una partida nueva se descarta la palabra de la anterior (mientras la categoría tenga más de una; si el pool se quedaría vacío, se permite repetir). La memoria dura la sesión.
- **Texto grande y de alto contraste** (WCAG AA), objetivos táctiles grandes y colores **aptos para daltonismo** (el color nunca es el único canal: siempre con icono + texto + forma).
- **Primer participante justo**: sin pistas, nunca empieza el impostor (quien no sabe nada); con pistas, puede empezar cualquiera.
- Confirmaciones antes de acciones destructivas (salir de la partida, eliminar jugador, votación).
- Pantalla **"Cómo se juega"** con instrucciones paso a paso.
- Feedback háptico al revelar el rol.
- Recuerda jugadores y categoría de la última partida.
- **Multidioma (v0.4.0)**: interfaz en **inglés (en-GB, idioma por defecto y de respaldo)** y **español (es-ES)**, con **selector de idioma dentro de la app** en todas las pantallas (junto al botón de tema). Si ya hay jugadores añadidos, la app **avisa antes de cambiar** de idioma: el cambio reinicia la pantalla y perdería los nombres.
- **Contenido del juego traducible y editable por la comunidad**: las 110 palabras y sus pistas (11 categorías) viven en [`app/src/main/assets/words/`](app/src/main/assets/words) (`es-ES.json`, `en-GB.json`), fáciles de editar desde GitHub; [`tools/validate_words.py`](tools/validate_words.py) valida el formato y la coherencia entre idiomas, y los PR de idioma requieren la revisión del code owner (`.github/`).

## Bugs corregidos

La primera prueba en dispositivo (2026-08-26) detectó 4 bugs de la fase 2, **todos corregidos y verificados el 2026-08-28** (incluido el crítico de fuga visual del rol en la animación de la carta). La fase 2.5 añadió BUG-5 (pie que repetía al jugador activo) y BUG-6 (crash por `SENSOR_DELAY_FASTEST` sin permiso). La fase 3 añadió BUG-7: al cambiar de idioma se perdían los jugadores añadidos (ahora la app avisa y pide confirmación). Todos corregidos y verificados en dispositivo. Registro completo en [`docs/BUGS.md`](docs/BUGS.md).

## Stack

| Tecnología | Versión |
|---|---|
| Kotlin + Jetpack Compose | Material 3 (BOM 2024.10.00) |
| Android Gradle Plugin | 9.3.0 (Kotlin integrado en AGP) |
| Gradle | 9.7.1 (wrapper incluido; disponible también como `gradle` de sistema) |
| minSdk / targetSdk / compileSdk | 24 / 36 / 36 |

Las versiones están centralizadas en el version catalog [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Compilar

```bash
# Linux / macOS / Windows (con JDK 17+ y Android SDK)
./gradlew :app:assembleDebug

# Termux (Android) — el shebang de gradlew no resuelve; usar sh
pkg install openjdk-17 aapt2   # una vez
sh gradlew :app:assembleDebug  # offline una vez cacheada la distribución de Gradle
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`. En Termux, cópialo a Descargas para instalarlo:

```bash
cp app/build/outputs/apk/debug/app-debug.apk ~/storage/downloads/
```

Los APKs de las versiones publicadas están en [Releases](https://github.com/manuti/impostor/releases).

## Estructura

```
app/src/main/kotlin/com/impostor/game/
├── MainActivity.kt          # Entrada de la app (AppCompatActivity: appcompat para el selector de idioma)
├── ui/App.kt                # Máquina de estados (setup → roleReveal → playing → ended) + tema persistido
├── ui/screens/              # SetupScreen, RoleRevealScreen, GameScreen, EndGameScreen
├── ui/components/           # Componentes compartidos (ThemeToggle sol/luna, LanguageButton, MotionDetector)
├── ui/theme/                # Tema oscuro/claro Material 3 + colores semánticos del juego
└── game/                    # Modelos y acceso al contenido (GameModels.kt, WordRepository.kt)
app/src/main/assets/words/   # Contenido del juego por idioma (es-ES.json, en-GB.json): 110 palabras y pistas
app/src/main/res/values/     # Cadenas de la UI en inglés (en-GB, por defecto y de respaldo)
app/src/main/res/values-es/  # Cadenas de la UI en español (es-ES)
app/src/main/res/xml/locales_config.xml  # Idiomas disponibles en el selector del sistema (es-ES, en-GB)
tools/validate_words.py      # Valida el contenido (esquema + coherencia entre idiomas); lo usa el CI
.github/                     # CODEOWNERS (revisión obligatoria de los PR de idioma), workflow y plantilla de PR
docs/investigacion.md        # Investigación previa (reglas, referencias, comparativa)
docs/PLAN-FASE2.md           # Plan de usabilidad (v0.3.0) y decisiones de diseño
docs/PLAN-FASE2.5.md         # Plan de mejoras intermedias (v0.3.1–v0.3.4)
docs/PLAN-FASE3.md           # Plan de la fase 3: C-1 (pre-i18n, v0.3.5) publicada + i18n (v0.4.0) con reglas vinculantes
docs/PLAN-FASE4.md           # Plan de publicación en Google Play (lecciones + checklist)
gradle/libs.versions.toml    # Version catalog
referencias/                 # Clon local de la referencia React (no se sube)
```

## Hoja de ruta

- [x] v0.1.0 — MVP local funcional (pass & play)
- [x] v0.2.0 — Selector de categorías corregido (ExposedDropdownMenuBox)
- [x] **v0.3.0 — Fase 2: usabilidad para personas mayores** (carta giratoria, tema claro/oscuro, texto grande, contraste AA, daltonismo, confirmaciones, primer participante justo)
- [x] **v0.3.1–v0.3.4 — Fase 2.5: mejoras intermedias** (configuración reordenada con pista off por defecto, toggle de tema en todas las pantallas, jugador activo destacado, carta oculta al mover el dispositivo)
- [x] **v0.3.5 — Fase 3: mejora pre-i18n (C-1)** (la palabra secreta no se repite entre partidas consecutivas)
- [x] **v0.4.0 — Fase 3: internacionalización (i18n)** (interfaz en inglés y español con selector dentro de la app y aviso al cambiar de idioma; contenido del juego traducible en JSON)
- [ ] Fase 4 — Publicación en Google Play: punto de partida y checklist en [`docs/PLAN-FASE4.md`](docs/PLAN-FASE4.md)

## Agradecimientos

- [Sergiolpz-dev/Impostor-juego](https://github.com/Sergiolpz-dev/Impostor-juego) — referencia de mecánicas y flujo (proyecto React/PWA; sin licencia, usado solo como referencia de estudio en `referencias/`).
- [imposter.app](https://imposter.app/es/) — reglas y versión online de referencia.
- Repos comparados durante la investigación: find-the-impostor, imposterhunt, undercover-word-game, suswords, ImpostorMX, imposter-party-game-IOS- (ver `docs/investigacion.md`).

## Licencia

**CC BY-NC 4.0** — Creative Commons Atribución-NoComercial 4.0 Internacional.

- ✅ Uso educativo y personal permitido.
- ✅ Derivaciones y modificaciones permitidas, con atribución.
- ❌ Uso comercial no permitido.

Texto completo en [LICENSE](LICENSE). Resumen legible: https://creativecommons.org/licenses/by-nc/4.0/
