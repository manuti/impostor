# Plan — Fase 2: usabilidad para personas mayores (v0.3.0)

> **Estado: APROBADO para implementación (2026-08-26)**. Requisitos añadidos tras la aprobación: animación de naipe (§3.14) y primer participante según pistas (§3.15).
> Decisiones D1–D3 y requisitos de color tomados por el usuario (2026-08-26).
> **Regla de fuentes (usuario, 2026-08-26)**: considerar **todos los mockups** (html y .md, y la recreación de los PNG), salvo lo **contradictorio o erróneo** — cada exclusión se documenta en §2.6.

---

## 1. Origen y método

- Base: `docs/CONTEXTO-FASE2.md` §6 (10 puntos de partida), `docs/investigacion.md` §6, y **todos los mockups** del usuario: Claude (`html`+`md`), DeepSeek (`html`+`md`), ChatGPT (`png`+`md`), Gemini (`png`+`md`) y la recreación `mockups/recreacion-mockups.html` (render fiel de los PNG).
- Regla de filtrado (usuario): se incorpora todo lo que aporten salvo lo contradictorio o erróneo (§2.6).
- **Regla general**: no tocar la mecánica del juego (reglas en `docs/investigacion.md` §1) salvo aprobación explícita.
- Las mejoras se diseñan para **personas mayores**: texto grande, contraste, objetivos táctiles grandes, sin gestos complejos, instrucciones paso a paso.

## 2. Revisión de mockups (todas las fuentes)

### 2.1 Claude — `mockups/mockup-claude.html` + `mockups/mockup-claude.md` (oscuro, conservador)

- Mantiene **"Mantén pulsado"**, pero **saca la pista del círculo táctil**: tarjeta fija arriba con el texto, círculo de 190dp abajo solo como interruptor. **El dedo nunca tapa la lectura.**
- Mayor contraste (tarjeta rojo oscuro, texto blanco 19px), **icono de daga + color** para "ERES IMPOSTOR" (daltonismo), botón con icono de ojo + texto, texto de ayuda explicando el gesto.
- Recomienda: **no forzar orientación horizontal**; tap como alternativa configurable al hold; subir contraste de textos secundarios; **X de eliminar grandes con confirmación**; ajuste de tamaño de fuente propio.

**→ Incorporado**: separación lectura/acción, icono+color, botón descriptivo, textos secundarios legibles, confirmación al borrar, ajuste de fuente (D4), vertical.
**→ Excluido**: el gesto "mantén pulsado" (contradice la decisión D2).

### 2.2 DeepSeek — `mockups/mockup-DeepSeek.html` + `mockups/mockup-DeepSeek.md` (claro, ruptura)

- Tema **blanco puro**, tipografía gigante (nombres 48px, botones 28–32px), sin grises sobre negro.
- **Tap-to-toggle**: botón grande "TOCA PARA VER TU ROL" / "TOCA PARA OCULTAR" (100px), verde de éxito, **vibración háptica** al revelar.
- Estado bloqueado explícito: **candado 🔒 dentro de "Siguiente Jugador"** + texto ámbar "Primero debes ver tu rol" (el botón deshabilitado no parece "muerto").
- Estructura: zona superior de lectura libre / zona inferior de acción cómoda para el pulgar.

**→ Incorporado**: tap-to-toggle (D2), tipografía gigante, estados explícitos con candado, feedback háptico, zona de acción para el pulgar.
**→ Excluido**: el **tema claro como única opción** (contradice D1: oscuro por defecto con opción claro; su diseño se aprovecha como la "opción claro" del toggle).

### 2.3 ChatGPT — `mockups/mockup-chatgpt.png` + `mockups/mockup-chatgpt.md` (modo fácil / tablet)

- ⚠️ **Error detectado**: `mockups/mockup-chatgpt.md` documenta el diseño "Modo Fácil para Personas Mayores", que es exactamente el HTML de DeepSeek (coincide textualmente). Como fuente independiente es **redundante/erróneo** — su contenido se atribuye a DeepSeek (§2.2).
- El aporte único de ChatGPT es el **PNG tablet** (visible con fidelidad en la recreación §2.5): barra superior "← Salir | Jugador 1 de 3 | ? Cómo se juega", tarjeta de pista separada, CTA inferior, barra inferior de instrucciones ("Debes ver tu rol antes de continuar" / "Pasa el móvil al siguiente jugador").
- Su `.md` también recomienda **orientación horizontal forzada** durante la partida y simplificar la configuración agrupando opciones.

**→ Incorporado**: patrón de barra superior con "Cómo se juega", barra de instrucciones, configuración agrupada por secciones.
**→ Excluido**: el gesto "MANTÉN PULSADO" del tablet (contradice D2) y el **landscape forzado** (contradice D3).

### 2.4 Gemini — `mockups/mockup-gemini.png` + `mockups/mockup-gemini.md` (oscuro refinado)

- Jerarquía de estados: candado "TOCA PARA VER" → fondo rojo "ERES IMPOSTOR" → **verde check "HAS VISTO TU ROL"**.
- Agranda **X de eliminar** y **selectores +/−**; sube opacidad de textos secundarios; estados clave en mayúsculas y negrita.
- Recomienda **vertical** para pass & play (landscape solo en tablet, doble columna: lectura izquierda / acción derecha).
- ⚠️ **Discrepancia**: el `.md` afirma que las X se encapsularon en círculos rojos **de mayor tamaño** con borde contrastado, pero la recreación del PNG las muestra pequeñas (~16px). La afirmación del `.md` **no se sostiene sobre la imagen** → no se usa como evidencia; el requisito de objetivo táctil ≥ 48dp se mantiene como exigencia de accesibilidad (la app debe cumplirla aunque el mockup no lo haga).

**→ Incorporado**: flujo en 3 estados, vertical (D3), +/− ampliados, textos secundarios legibles.
**→ Excluido**: nada por contradicción; la "X grande" pasa de evidencia a requisito.

### 2.5 Recreación — `mockups/recreacion-mockups.html` (render fiel de los PNG)

- **Vista tablet (ChatGPT)**: barra superior "← Salir | Jugador 1 de 3 | ? Cómo se juega", puntos de progreso, tarjeta de pista con rol ("ERES IMPOSTOR" + icono), CTA inferior "MANTÉN PULSADO PARA REVELAR" (gesto descartado por D2), barra inferior de instrucciones.
- **Colección móvil (Gemini)**: configuración (añadir jugador → lista con ✕ → stepper de impostores −/+ → switch "Pista para el impostor" con subtítulo "El impostor verá una pista" → "Categoría de palabras" → "Empezar Partida") y **revelado en 3 estados** (bloqueado: candado + "TOCA PARA VER" ámbar + "SIGUIENTE JUGADOR" deshabilitado con nota "Por favor, mira tu rol antes de continuar" → revelado: círculo rojo con rol + pista → confirmado: check verde "HAS VISTO TU ROL / Puedes continuar" + "Siguiente Jugador" activado). "✕ CANCELAR" arriba en las pantallas de rol.

**Conclusiones que se extraen de la recreación**: tap-to-toggle en móvil; flujo en 3 estados; la lectura no depende de la posición del dedo (con tap se suelta y se lee); acción ("Siguiente Jugador") siempre debajo y estable; patrón de barra superior y pie de instrucciones; estructura de configuración completa.

### 2.6 Excluidos por contradicción o error (regla del usuario)

| Fuente | Contenido excluido | Motivo |
|---|---|---|
| Claude, tablet ChatGPT | "Mantén pulsado" para revelar | Contradice decisión D2 (tap-to-toggle) |
| DeepSeek, ChatGPT (md) | Tema claro como única opción | Contradice decisión D1 (oscuro por defecto + opción claro) |
| ChatGPT (md) | Landscape forzado | Contradice decisión D3 (vertical) |
| `mockups/mockup-chatgpt.md` | Todo su contenido como fuente independiente | Erróneo: duplica el diseño de DeepSeek ("Modo Fácil") |
| Gemini (md) | "X de eliminar grandes" como evidencia del mockup | Erróneo: la imagen (recreación) las muestra pequeñas; se conserva como requisito de accesibilidad |

## 3. Requisitos consolidados (implementar sí o sí)

1. **Tap-to-toggle** en la revelación (D2; DeepSeek/ChatGPT/Gemini).
2. **Flujo de revelado en 3 estados** (bloqueado → revelado → confirmado) con "Siguiente Jugador" bloqueado hasta ver el rol (Gemini/recreación; DeepSeek).
3. **La lectura nunca depende de la posición del dedo** (Claude; principio común): con tap se suelta y se lee; el texto puede vivir en el círculo central (referencia móvil) sin que el dedo lo tape.
4. **Patrón de barra superior** "← Salir | Jugador X de N | Cómo se juega" + **pie de instrucciones** ("Debes ver tu rol antes de continuar" / "Pasa el móvil al siguiente jugador") (tablet ChatGPT; Claude).
5. **Tipografía grande y escalable** (sp; mínimo 16sp en contenido; títulos/roles mucho mayores) (todos los mockups).
6. **Alto contraste WCAG AA** y **colores aptos para daltonismo** (D5): color nunca canal único; icono + texto + forma (todos proponen iconos además del color).
7. **Objetivos táctiles** ≥ 48dp (ideal 56dp+); primarios muy grandes (referencia 80–100dp en los mockups) (DeepSeek/ChatGPT; D5).
8. **Estados explícitos y feedback**: botón deshabilitado con candado + texto visible (no "muerto"); **feedback háptico** al revelar si el dispositivo lo permite (DeepSeek).
9. **La app como moderadora**: "Jugador X de N", "Pasa el móvil al siguiente jugador", entrada "Cómo se juega" (tablet; Claude).
10. **Confirmaciones** antes de acciones destructivas: Abandonar/Cancelar, eliminar jugador (la votación ya tiene) (Claude; ChatGPT).
11. **SetupScreen simplificado**: estructura de §2.5 y agrupación por secciones (Gemini; ChatGPT).
12. **Estabilidad del layout** (D3, requisito usuario): botones con **tamaños y posiciones fijas** en toda la dinámica del juego.
13. **Ajuste de tamaño de fuente**: validar escala del sistema al 200%; ajuste propio solo si falla (D4; Claude lo sugiere como opción).
14. **Revelado con animación de naipe** (requisito usuario, 2026-08-26): la revelación se anima como un **naipe que gira sobre su eje vertical**. Anverso: **logo del ojo de la app ocupando todo el naipe**. Reverso: el texto de la **palabra** (civiles) o lo que corresponda al impostor (la pista si hay pistas; solo el rol si no).
15. **Primer participante según pistas** (requisito usuario, 2026-08-26; ajuste de orden aprobado explícitamente): **sin pistas** → el primer participante (quien abre el debate, `startingPlayer`) se elige **aleatoriamente entre los que NO son impostor**, evitando empezar con quien no sabe nada; **con pistas activadas** → puede empezar cualquier jugador.

## 4. Decisiones de diseño (tomadas 2026-08-26) y pendientes

| ID | Decisión | Estado |
|---|---|---|
| D1 | Tema | ✅ **Decidido**: oscuro por defecto, con **opción de claro** mediante icono sol/luna (o similar) en la **parte superior** de la app |
| D2 | Gesto de revelado | ✅ **Decidido**: **tap-to-toggle** (recomendación aceptada); seguro = "Siguiente Jugador" bloqueado hasta ver el rol |
| D3 | Orientación | ✅ **Decidido**: **vertical**, con botones de **tamaños y posiciones fijas** en toda la dinámica del juego |
| D4 | Ajuste de tamaño de fuente | ⏳ Por defecto: escala del sistema (validar 200% en dispositivo); ajuste propio solo si falla la prueba |
| D5 | Colores | ✅ **Requisito del usuario**: alto contraste (WCAG AA) y **aptos para daltonismo** — color nunca como canal único |

Referencias: mockups de §2 (con exclusiones de §2.6) + `mockups/recreacion-mockups.html`.

## 5. Cambios por pantalla (lista definitiva de trabajo)

### 5.0 Infraestructura transversal (primero)

- **Externalizar todas las cadenas de UI** a `res/values/strings.xml` y usar `stringResource()` — reglas i18n en `docs/PLAN-FASE3.md` §4 (vinculantes).
- **Tema oscuro/claro [D1]**: colores literales (`0xFFF87171`, etc.) → tokens/`ColorScheme` dual (oscuro y claro) con contraste AA verificado; toggle con **icono sol/luna** en la parte superior de todas las pantallas; **persistir** la preferencia. El tema claro se diseña según la propuesta de DeepSeek (§2.2).
- **Paleta daltonismo [D5]**: validar combinaciones rojo/verde/azul/morado con simuladores; toda distinción por color se refuerza con icono + texto + forma.
- **Estabilidad de layout [D3]**: definir una sola vez los tamaños y posiciones de los botones (acción, siguiente, eliminar, steppers) y reutilizarlos en todas las pantallas y estados del juego; prohibido que un control se mueva o cambie de tamaño entre jugadores o estados.
- **Tipografía**: sp con escala base mayor; estilos tipográficos reutilizables (título, rol, pista, helper, botón).
- **Objetivos táctiles**: revisar todos los elementos interactivos ≥ 48dp (ideal 56dp+).

### 5.1 `RoleRevealScreen.kt` — prioridad máxima

- **Revelado con animación de naipe** (requisito §3.14): el elemento de revelado es un **naipe** que gira sobre su **eje vertical** (`rotationY` con perspectiva 3D). **Anverso**: logo del ojo de la app ocupando todo el naipe. **Reverso**: la palabra (civiles) o la pista/rol (impostor). Tap en el naipe → animación de giro (media vuelta); al pasar al siguiente jugador vuelve al anverso.
- **Flujo de estados** (adaptado de la referencia Gemini §2.5): **bloqueado** = naipe con el ojo (con "TOCA PARA VER" si procede) → **revelado** = naipe girado con el contenido → **confirmado** = estado verde "HAS VISTO TU ROL" que activa "Siguiente Jugador".
- [D2] **Tap-to-toggle**: un toque en el naipe alterna anverso/reverso; el dedo solo está en pantalla durante el toque, nunca mientras se lee (requisito 3).
- **Logo del ojo**: hoy la app no tiene logo de ojo (solo `ic_launcher.xml`); crear un **vector drawable** del ojo (referencia visual: icono de ojo de los mockups) y usarlo en el anverso del naipe.
- **Implementación de la animación** (nota técnica): `graphicsLayer { rotationY }` animado con `animateFloatAsState`/`Animatable`, `cameraDistance` para la perspectiva 3D; intercambiar anverso/reverso en los 90°; `contentDescription` según el estado para TalkBack.
- **"Siguiente Jugador"** deshabilitado (candado + nota visible, p. ej. "Por favor, mira tu rol antes de continuar") hasta el estado confirmado; al confirmar se activa. Posición estable bajo el área de lectura [D3].
- **Feedback háptico** al revelar, si el dispositivo lo permite (DeepSeek).
- Progreso "Jugador X de N" + puntos, y nombre del jugador en grande.
- **Barra superior** (patrón tablet adaptado a móvil): **← Salir** | **Jugador X de N** | **Cómo se juega** + toggle sol/luna [D1]. ("Cómo se juega" = punto 9; alcance mínimo: pantalla sencilla de instrucciones).
- **Pie de instrucciones**: "Debes ver tu rol antes de continuar" / "Pasa el móvil al siguiente jugador".
- **✕ CANCELAR** disponible durante la revelación, con confirmación antes de abandonar.
- [D3] El círculo de revelado y el botón "Siguiente Jugador" mantienen **tamaño y posición idénticos en cada jugador** de la partida.

### 5.2 `SetupScreen.kt`

- **Estructura** (referencia Gemini/recreación §2.5, agrupada por secciones según ChatGPT): título "El Impostor / Configura la partida" → **¿Quién va a jugar?** (campo + botón "Añadir", lista con ✕) → **¿Cuántos impostores?** (stepper − / +) → **¿Pista para el impostor?** (switch con subtítulo "El impostor verá una pista") → **Categoría de palabras** → **Empezar Partida**.
- Steppers − / + grandes (objetivo táctil ≥ 56dp) con mensaje de máximo legible.
- Botones ✕ de eliminar jugador con objetivo táctil ≥ 48dp y **confirmación antes de borrar** (requisito 10; ver discrepancia en §2.4).
- Selector de categoría evidente: etiqueta "CATEGORÍA" + valor ("Todas las categorías ▼"); se mantiene `ExposedDropdownMenuBox` (M3, accesible con TalkBack).

### 5.3 `GameScreen.kt`

- Botones de jugador grandes con estados vivos/eliminados claros (icono + texto, no solo color).
- Confirmación de voto (ya existe) con botones grandes y texto claro.
- "Abandonar" → "← Salir" con confirmación ("¿Seguro que quieres abandonar la partida?" / CANCELAR | ABANDONAR).
- Contadores vivos/eliminados legibles; categoría visible.

### 5.4 `EndGameScreen.kt`

- Mismo tratamiento de contraste y tipografía; palabra secreta y roles revelados en tarjetas grandes y legibles.

### 5.5 `App.kt` — primer participante según pistas (requisito §3.15)

- Hoy: `startingPlayer = gameState.players.random().name` (aleatorio entre **todos**).
- Nuevo: en `allPlayersSawRoles()`, si `config.showHintToImpostor` es `false` → `gameState.players.filter { it.role != Role.IMPOSTOR }.random()`; si es `true` → `gameState.players.random()` (comportamiento actual).
- Es el único cambio de mecánica de esta fase y está **aprobado explícitamente por el usuario** (2026-08-26). El orden de revelado de roles (secuencial) no cambia; `startingPlayer` se muestra en `GameScreen` al inicio del debate.

## 6. Checklist de verificación (probar en dispositivo real tras cada cambio)

- [ ] TalkBack activado: toda la app navegable, `contentDescription` en iconos, orden lógico.
- [ ] Escala de fuente del sistema al 200%: nada se corta ni se solapa.
- [ ] Contraste: leer textos sobre fondos de color en estado normal y pulsado (WCAG AA).
- [ ] Daltonismo: revisar con simulador de daltonismo (rojo/verde/azul) que ninguna información dependa solo del color.
- [ ] Tamaño táctil: todos los objetivos ≥ 48dp (ideal 56dp+).
- [ ] Animación de naipe: el giro es fluido (sin saltos) en dispositivo; anverso (ojo) y reverso (palabra/pista) correctos.
- [ ] Primer participante: sin pistas nunca es el impostor; con pistas puede ser cualquiera (probar varias partidas).
- [ ] El flujo completo de una partida se juega sin ayuda externa (la app guía cada paso).
- [ ] Prueba con una persona mayor real si es posible (objetivo final).

## 7. Cierre de la fase

- Cambios pequeños e incrementales; compilar (`sh gradlew :app:assembleDebug`) y probar en dispositivo tras cada uno.
- UI en español (es-419); nada de jerga ("blufear", "pass & play").
- Al terminar: actualizar `README.md`, versionar **v0.3.0** (incrementar `versionCode`), publicar release con el APK nuevo, commitear.
