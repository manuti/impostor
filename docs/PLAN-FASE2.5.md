# Contexto y plan — Fase 2.5: mejoras intermedias (v0.3.x)

> Documento de arranque de sesión. **Léelo completo antes de tocar código.**
> Creado: 2026-08-28 · Actualizado: 2026-09-02 (fase **cerrada**: v0.3.1–v0.3.4 publicadas) · Proyecto: El Impostor (Android nativo) · Repo: https://github.com/manuti/impostor

---

## 1. Objetivo y estado

Fase intermedia entre la **fase 2** (usabilidad, v0.3.0 **cerrada y publicada**) y la **fase 3** (i18n). Recoge 4 mejoras detectadas tras el uso real, planificadas para que **no compliquen la fase 3 (i18n)** ni la **fase 4 (publicación en Google Play)**.

- **Estado del plan**: ✅ **cerrado el 2026-09-02** — M-4 (v0.3.1), M-3 (v0.3.2), M-2 (v0.3.3) y M-1 (v0.3.4) implementadas, probadas en dispositivo y publicadas. Cierre de fase: README y PLAN-FASE4 actualizados (lecciones + checklist de Play Store).
- Versionado: cada mejora (o grupo) → **v0.3.1, v0.3.2, …** (incremento de patch + `versionCode` +1 por release).

## 2. Estado actual del proyecto (v0.3.0)

- App publicada: release **v0.3.0** (commit `e189809`) con APK `el-impostor-v0.3.0.apk`. Repo público `manuti/impostor`, rama `main`.
- **Bugs de fase 2**: los 4 detectados están corregidos y verificados (`docs/BUGS.md`).
- **Stack**: Kotlin + Jetpack Compose (Material 3, BOM 2024.10.00), AGP 9.3.0 (Kotlin integrado), Gradle 9.7.1 (wrapper + `gradle` de sistema), compileSdk 36 / minSdk 24, version catalog en `gradle/libs.versions.toml`.
- **i18n-ready**: todas las cadenas de UI en `res/values/strings.xml` (es-419), con placeholders y plurals; **prohibido añadir strings hardcodeados** (regla §5).
- **Licencia**: CC BY-NC 4.0.
- Los mockups y recreaciones viven en `mockups/` (ignorados en git, no se suben).

## 3. Cómo compilar, probar e instalar

```bash
# Termux (este dispositivo)
cd /data/data/com.termux/files/home/impostor
sh gradlew :app:assembleDebug          # offline (dist de Gradle cacheada); APK → app/build/outputs/apk/debug/app-debug.apk
cp app/build/outputs/apk/debug/app-debug.apk ~/storage/downloads/   # instalarlo desde Descargas
```

- Compilar con `./gradlew` en Linux/macOS/Windows (JDK 17+ y Android SDK).
- **Cada cambio se prueba en el dispositivo real** (no hay emulador).
- Publicar release (con `gh`, autenticado como `manuti`):
  `gh release create v0.3.X --title "..." --notes-file notas.md "app/build/outputs/apk/debug/app-debug.apk#el-impostor-v0.3.X.apk"`
  (nota: el renombrado del asset con `#` hay que verificar; si falla, subir copia renombrada y borrar el asset mal nombrado).

## 4. Arquitectura (dónde está cada cosa)

```
app/src/main/kotlin/com/impostor/game/
├── MainActivity.kt          # Entrada; setContent { App() } (el tema lo gestiona App)
├── ui/App.kt                # Máquina de estados + estado de tema (SharedPreferences) + CompositionLocals
├── ui/components/           # ThemeToggle.kt (botón sol/luna, reutilizable)
├── ui/screens/
│   ├── SetupScreen.kt       # Configuración (M-4 reordena aquí; switch de pista con estado inicial)
│   ├── RoleRevealScreen.kt  # Naipe giratorio (Animatable), 3 estados, dialogs; M-1/M-2 tocan aquí
│   ├── GameScreen.kt        # Debate/votación; M-3 añade toggle a su barra superior
│   └── EndGameScreen.kt     # Fin; M-3 añade toggle a su barra superior
├── ui/theme/Theme.kt        # Tema oscuro/claro, GameColors (tokens semánticos), LocalDarkTheme/LocalOnToggleTheme
└── game/                    # GameModels.kt (datos puros), GameWords.kt (110 palabras, 11 categorías)
app/src/main/res/values/strings.xml   # TODAS las cadenas de la UI
app/src/main/res/drawable/            # ic_eye, ic_sun, ic_moon, ic_help, ic_launcher
docs/                          # CONTEXTO-FASE2.md, PLAN-FASE2.md, PLAN-FASE2.5.md (este), PLAN-FASE3.md, BUGS.md, investigacion.md
```

Notas clave:
- `GameModels.kt` son **datos puros**; el estado vive en `App.kt`; jugadores con `UUID` estable.
- El tema se expone a las pantallas vía `LocalDarkTheme` / `LocalOnToggleTheme` (CompositionLocal).
- `RoleRevealScreen` usa `Animatable` keyeado por jugador: al cambiar de jugador el naipe nace en 0° (anverso) **sin animación** (lección BUG-1: nunca animar el giro de vuelta con contenido nuevo).

## 5. Reglas vinculantes

1. **i18n (fase 3)** — reglas de `docs/PLAN-FASE3.md` §4: todo texto nuevo a `strings.xml`, `stringResource()`, placeholders `%1$s`, `<plurals>`, sp, `start/end`, sin gramática española en lógica, contenido de juego separado de la UI.
2. **Fase 4 (Google Play)** — preparación:
   - Modelos como datos puros; acciones como funciones puras sobre `GameState`.
   - Cualquier **sensor/periférico nuevo** (p. ej. acelerómetro) aislado en componente reutilizable con ciclo de vida gestionado (`DisposableEffect`), sin lógica de negocio dentro.
   - No añadir dependencias de analytics/telemetría en esta fase (telemetrydeck es fase 4).
3. Cambios pequeños; compilar y probar en dispositivo tras cada uno; UI en es-419.

## 6. Decisiones tomadas

| ID | Decisión |
|---|---|
| M-1 (turno al mover) | **Opción A**: al detectar movimiento brusco con la carta revelada, se oculta la carta y se **conserva el mismo jugador** (el botón "Siguiente Jugador" sigue disponible) |
| Versionado | v0.3.1, v0.3.2, … (patch por mejora; `versionCode` +1) |
| Orden de trabajo | M-4 → M-3 → M-2 → M-1 (de simple a complejo) |

## 7. Mejoras a implementar

### M-4 · Reordenar la configuración de la partida (`SetupScreen.kt`) — v0.3.1 ✅

- Nuevo orden: **Título** → **bloque "Configuración de la partida"** (nº impostores, pista, categoría) → **Empezar partida** → **bloque "Añadir jugadores"** (campo + lista con ✕).
- **Pista del impostor DESACTIVADA por defecto**: cambiar el estado inicial del switch a `false` (hoy `true`).
- Strings nuevos (etiquetas de bloque) → `strings.xml`.
- Verificación: configurar una partida siguiendo el nuevo orden; pista apagada por defecto.
- **Nota de implementación (2026-08-31)**: por petición del usuario, el orden interno del bloque de configuración quedó **Categoría de palabras → Nº de impostores → Pista** (categoría primero). El subtítulo `setup_subtitle` se eliminó (redundante con la cabecera de bloque). Publicada como v0.3.1.

### M-3 · Toggle claro/oscuro en todas las pantallas — v0.3.2 ✅

- `ThemeToggleButton` (ya existe) se coloca en la barra superior de `SetupScreen`, `GameScreen` y `EndGameScreen` (RoleRevealScreen ya lo tiene).
- `contentDescription` ya está en `strings.xml`.
- Verificación: alternar tema desde cada pantalla; preferencia persistida.

### M-2 · Reforzar el nombre del jugador activo en la revelación (`RoleRevealScreen.kt`) — v0.3.3 ✅

- El nombre del jugador pasa a una **tarjeta/píldora destacada** (fondo de contraste, tamaño grande).
- Repetir el nombre en el pie: "Pasa el móvil a **NOMBRE**" (placeholder en `strings.xml`).
- Verificación: al pasar de jugador, el nombre activo es evidente en toda la pantalla.

### M-1 · Ocultar la carta al detectar movimiento brusco tras revelar (`RoleRevealScreen.kt`) — v0.3.4 ✅

- Sensor de movimiento: `SensorManager` + `TYPE_LINEAR_ACCELERATION` en `RoleRevealScreen`, registro/desregistro con `DisposableEffect`, **activo solo cuando la carta está revelada**.
- Umbral de sacudida calibrado (evitar falsos positivos al tocar).
- Al detectarse: la carta vuelve al anverso **sin animación** (patrón BUG-1) y se muestra un aviso breve ("Carta oculta. Pulsa Siguiente o pasa el móvil" → `strings.xml`). **Se conserva el mismo jugador** (decisión §6).
- Componente de sensor aislado y reutilizable (regla §5.2).
- Verificación: revelar → mover el móvil bruscamente → la carta se oculta y el turno sigue siendo el mismo; mover el móvil SIN revelar no hace nada; tocar la carta no dispara el sensor.
- **Notas de implementación (2026-09-02)**: el componente es `MotionDetector` (`ui/components/MotionDetector.kt`) con **acelerómetro lineal (3 m/s²) + giroscopio (2 rad/s)** — el giro de pasar el móvil entre jugadores no lo ve el acelerómetro solo (calibrado en dispositivo). Usa `SENSOR_DELAY_GAME`: `SENSOR_DELAY_FASTEST` (0 µs) exige el permiso `HIGH_SAMPLING_RATE_SENSORS` desde API 31 y crasheaba sin él (BUG-6). Un listener por sensor, sin tocar `event.sensor`. Sin permisos nuevos en el manifest.

## 8. Cierre por mejora y de fase

- Cada mejora: implementar → compilar → probar en dispositivo → **commit + release v0.3.X** (con su APK) → actualizar `docs/BUGS.md` si aplica.
- Al cerrar la fase 2.5: actualizar `README.md` y **la información de la fase 4** (lecciones, checklist de requisitos de Play Store) según el cierre de fases 2 y 2.5.

## 9. Checklist de verificación (cada cambio)

- [ ] Compila sin errores (`sh gradlew :app:assembleDebug`).
- [ ] Probado en dispositivo real.
- [ ] Sin strings hardcodeados nuevos (grep rápido o revisión visual del diff).
- [ ] Contraste AA y targets ≥ 48dp en lo tocado.
- [ ] TalkBack: `contentDescription` en elementos nuevos.

## 10. Referencias

- `docs/CONTEXTO-FASE2.md` — contexto de la fase 2 (cerrada).
- `docs/PLAN-FASE2.md` — plan de usabilidad (cerrado).
- `docs/PLAN-FASE3.md` — plan i18n con reglas vinculantes (§4).
- `docs/BUGS.md` — registro de bugs (todos resueltos).
- `docs/investigacion.md` — reglas del juego e investigación.
