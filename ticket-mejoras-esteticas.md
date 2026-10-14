# Mejoras estéticas manteniendo WCAG y usabilidad para personas mayores

## Contexto
Revisión de capturas de: `SetupScreen`, `RoleRevealScreen`, `GameScreen` (Debate/votación) y `EndGameScreen`, en modo claro y oscuro. Referencia de tokens actuales en `app/src/main/kotlin/com/impostor/game/ui/theme/Theme.kt` (`DarkColors`/`LightColors` de Material 3 y `DarkGameColors`/`LightGameColors` semánticos del juego).

La base es sólida: tipografía grande (`AppTypography`, contenido ≥16sp), formas pill consistentes, buen espaciado, y el patrón "color nunca es el único canal" ya se aplica bien en varios sitios (ojo + texto en "TOCA PARA VER", check + texto en "HAS VISTO TU ROL", escudo + texto en "¡Victoria Civil!"). Los puntos siguientes son pulido, no rediseño.

---

### 1. Jerarquía visual en `SetupScreen`
Añadir fondo `surfaceVariant` (ya existe como token, `0xFF26262F` oscuro / `0xFFEDEDF2` claro) a modo de tarjeta para agrupar "Categoría de palabras", "Número de impostores" y "Pista para el impostor". Ahora todo comparte el mismo `background` y cuesta separar secciones de un vistazo. Alternativa más ligera: `Divider` de 1dp con `onSurfaceVariant` a baja opacidad entre secciones.

### 2. Botón deshabilitado ("Empezar Partida" / "Siguiente Jugador")
Texto gris sobre fondo gris casi ilegible cuando está disabled. WCAG exime a elementos disabled del ratio 4.5:1, pero perjudica baja visión. Sugerencia: usar `OutlinedButton` con borde `primary` a baja opacidad en vez de relleno plano, manteniendo el texto en `onSurfaceVariant` para que se lea "inactivo" sin ser invisible.

Además, añadir un micro-texto informativo bajo el botón mientras esté deshabilitado (ej. "Añade al menos 3 jugadores para comenzar"), coherente con el patrón que `SetupScreen` ya usa en el contador de jugadores ("Necesitas al menos 3 jugadores"). En personas mayores, saber *por qué* algo no es interactivo reduce la frustración más que solo mejorar el contraste.

### 3. Consistencia de color en pills de jugador y tarjeta de rol
El pill del nombre de jugador y la tarjeta de rol cambian de tono entre capturas sin patrón aparente (no corresponden a `GameColors.impostor`/`civil`, que solo se revelan al final en `EndGameScreen`). Si es decorativo/aleatorio, fijarlo a un único estilo por tema (`primaryContainer`/`onPrimaryContainer`) para no sugerir que el color codifica algo durante la partida, cuando en realidad la identidad se mantiene oculta hasta el resultado.

### 4. Indicador de progreso (puntos de `RoleRevealScreen`)
Los puntos que marcan "jugador 2 de 3" son pequeños y dependen del color (verde/negro/gris) para transmitir estado. Para reforzar WCAG 1.4.1 (no depender solo del color):
- Aumentar tamaño a ≥8-10dp de diámetro.
- Diferenciar también por relleno/contorno (lleno = visto, contorno = actual, hueco = pendiente).
- Verificar contraste del verde de "visto" contra `background` en ambos temas.

### 5. Tarjeta "TOCA PARA VER" (`RoleRevealScreen`)
Ya combina icono (ojo) + texto — mantener. Verificar que el color del texto (parece usar `warn`, `0xFFF5C94A` oscuro / `0xFFB45309` claro) cumpla 4.5:1 sobre `cardFrontBg` en ambos temas; el tono claro (marrón sobre blanco) es el más justo de revisar.

### 6. Mensaje "Por favor, mira tu rol antes de continuar"
Usa `warn` sin icono. Añadir un icono ⚠ delante refuerza el aviso sin depender solo del color, igual que ya se hace con ✔ en "HAS VISTO TU ROL" (que usa `success`).

### 7. Texto "Pasa el móvil a [nombre]"
Correcto y claro. Verificar contraste de este texto secundario (`onSurfaceVariant` probablemente) sobre `background` — en las capturas se ve algo apagado comparado con el resto del texto.

### 8. Botón de tema (sol/luna) — `ThemeToggle.kt`
Confirmar que tiene `contentDescription`/`semantics` con texto accesible ("Cambiar a tema oscuro/claro"), no solo el icono, para lectores de pantalla. Mismo chequeo para `LanguageButton.kt`.

### 9. `GameScreen` — botones de jugador en Debate
Los botones "tt", "hh", "jj" no tienen indicador visual de que son votables más allá del texto "Pulsa en un jugador para votarlo", que queda lejos (al final de la pantalla) de los propios botones. Sugerencia: icono de voto junto a cada botón, o mover ese texto justo encima de la lista de jugadores vivos.

Layout: con número impar de jugadores queda una fila desequilibrada (dos + uno suelto). **Preferir lista vertical uniforme de tarjetas anchas** sobre "último botón a ancho completo": en una pantalla con contadores de "Civiles vivos"/"Impostores vivos" ya compitiendo por atención, un botón ancho completo al final de la cuadrícula puede leerse por error como el CTA principal de la pantalla.

### 10. Contador "Civiles vivos" / "Impostores vivos" (`GameScreen`)
Buen uso de color (`civil`/`impostor`) + etiqueta de texto — mantener. Verificar contraste de `civil` claro (`0xFF1D4ED8`) sobre `background` claro (`0xFFFDFDFD`); en las capturas se ve algo bajo comparado con el modo oscuro.

### 11. Diálogo "Eliminar jugador"
Bien estructurado: título, pregunta clara, dos acciones diferenciadas por color y posición (`error` para "Eliminar"). Mejora: dar foco inicial a "Cancelar" (la opción no destructiva) para evitar eliminaciones accidentales con doble-tap rápido, algo relevante para usuarios con menor destreza motora. Verificar contraste del texto "Eliminar" (`error`, `0xFFB91C1C` claro) sobre el fondo del diálogo — se ve algo apagado en la captura.

### 12. `EndGameScreen` — pantalla de referencia
La mejor resuelta de la app: icono + texto + color, agrupación en tarjeta, roles finales separados en "Impostores" (`impostorBg`/`onImpostorBg`) vs "Civiles" (`civilBg`/`onCivilBg`) con burbujas de color diferenciado. Usar como modelo para el resto de pantallas (punto 1).

Único ajuste: revisar contraste de las burbujas de nombre — `impostor` (`0xFFB91C1C`) sobre `impostorBg` (`0xFFFDE8E8`) en modo claro es el par que parece más justo de toda la app; medir con Rich Results / contrast checker y, si no llega a 4.5:1, oscurecer ligeramente el texto o aclarar menos el fondo.

### 13. Tipografía — jerarquía de títulos de sección
Reforzar la jerarquía entre títulos de sección ("Categoría de palabras", "Número de impostores") y contenido. **Matiz importante**: no subir a `FontWeight.Bold` en todos los títulos — si título y contenido son ambos muy prominentes en peso, se genera fatiga visual y ningún elemento destaca de verdad. Mejor mantener `SemiBold` (`titleMedium`/`titleSmall` de `AppTypography`) y reforzar la jerarquía mediante diferencia de escala (`sp`) o espaciado (`Spacer`) entre título y contenido, no mediante grosor.

### 14. Controles interactivos
Verificar área táctil ≥44×44dp en el `Switch` de "Pista para el impostor" aunque el thumb visual sea menor. Mantener el borde de foco `primary` visible en el `ExposedDropdownMenuBox` de categoría también para navegación por teclado/TalkBack.

### 15. Comportamiento del teclado — input "Nombre del jugador" (`SetupScreen`)
Al tocar el campo de texto, el teclado del sistema puede tapar el botón "Añadir", especialmente con fuente del sistema aumentada (perfil de usuario objetivo de esta app). Añadir `imePadding()` al contenedor del formulario para que el botón "Añadir" quede siempre visible por encima del teclado, en vez de desplazarse fuera de la pantalla visible.

---

## Fuera de alcance / ya bien resuelto (no tocar)
- Espaciado vertical generoso en todas las pantallas.
- Formas pill consistentes en botones e inputs.
- Uso de icono + color + texto en "HAS VISTO TU ROL", "TOCA PARA VER" y la X de borrar jugador.
- Tamaño de fuente base (`AppTypography`, todo ≥16sp).
- Estructura de `EndGameScreen` (ver punto 12 como referencia para el resto).
