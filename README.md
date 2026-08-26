# El Impostor 🎭

Juego de deducción social **nativo para Android** (Kotlin + Jetpack Compose) en el que todos los jugadores conocen una palabra secreta excepto uno: **el impostor**, que debe fingir y adivinar la palabra sin ser descubierto.

Versión actual: **v0.2.0** (modo local *pass & play*, un solo dispositivo que se pasa de mano en mano).

## Cómo se juega

1. **Configuración**: añade al menos 3 jugadores, elige nº de impostores, si el impostor recibe pista y la categoría de palabras.
2. **Reparto de roles**: cada jugador mantiene pulsado el círculo para ver en privado su rol (palabra secreta o "Eres IMPOSTOR" con pista).
3. **Debate**: el grupo da pistas de una palabra relacionada con la secreta, sin decirla.
4. **Votación**: todos votan quién creen que es el impostor.
5. **Victoria**: los civiles ganan si eliminan a todos los impostores; los impostores ganan si igualan o superan en número a los civiles.

> Reglas basadas en [imposter.app](https://imposter.app/es/how-to-play-imposter-game/). La investigación completa (reglas, versión online de referencia, comparativa de repos existentes y hoja de ruta) está en [`docs/investigacion.md`](docs/investigacion.md).

## Características actuales (v0.2.0)

- Modo local **pass & play** (un solo dispositivo).
- **110 palabras propias** en 11 categorías (Animales, Comida, Lugares, Objetos, Profesiones, Deportes, Países, Naturaleza, Conceptos, Fantasía, Películas).
- 1–N impostores configurables; pista opcional para el impostor.
- Selector de categorías funcional (v0.2.0, patrón ExposedDropdownMenuBox).
- Revelación privada de roles con *mantén pulsado*.
- Votación con confirmación y resultado de eliminación.
- Pantalla de fin con la palabra secreta y roles revelados.
- Tema oscuro (Material 3).

## Stack

| Tecnología | Versión |
|---|---|
| Kotlin + Jetpack Compose | Material 3 (BOM 2024.10.00) |
| Android Gradle Plugin | 9.3.0 (Kotlin integrado en AGP) |
| Gradle | 9.7.1 (wrapper incluido) |
| minSdk / targetSdk / compileSdk | 24 / 36 / 36 |

Las versiones están centralizadas en el version catalog [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Compilar

```bash
# Linux / macOS / Windows (con JDK 17+ y Android SDK)
./gradlew :app:assembleDebug

# Termux (Android) — el shebang de gradlew no resuelve; usar sh
pkg install openjdk-17 aapt2   # una vez
sh gradlew :app:assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`. En Termux, cópialo a Descargas para instalarlo:

```bash
cp app/build/outputs/apk/debug/app-debug.apk ~/storage/downloads/
```

Los APKs de las versiones publicadas están en [Releases](https://github.com/manuti/impostor/releases).

## Estructura

```
app/src/main/kotlin/com/impostor/game/
├── MainActivity.kt          # Entrada de la app
├── ui/App.kt                # Máquina de estados (setup → roleReveal → playing → ended)
├── ui/screens/              # SetupScreen, RoleRevealScreen, GameScreen, EndGameScreen
├── ui/theme/                # Tema oscuro Material 3
└── game/                    # Modelos y lista de palabras (GameModels.kt, GameWords.kt)
docs/investigacion.md        # Investigación previa (reglas, referencias, comparativa)
gradle/libs.versions.toml    # Version catalog
referencias/                 # Clon local de la referencia React (no se sube)
```

## Hoja de ruta

- [x] v0.1.0 — MVP local funcional (pass & play)
- [ ] **Fase 2 — usabilidad para personas mayores**: texto grande, alto contraste, botones grandes, tiempos ampliados, lectura en voz alta (TTS), lenguaje sencillo, tutorial guiado (puntos de partida en `docs/investigacion.md` §6)
- [ ] Modo online multijugador (valoración: backend propio o WebRTC)
- [ ] Versión iOS (SwiftUI) o PWA compartida

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
