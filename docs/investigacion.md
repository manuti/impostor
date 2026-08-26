# Investigación — Juego del Impostor (fase 1)

> Documento de investigación previo al desarrollo de nuestra propia versión del juego del impostor.
> Objetivo: Android nativo (primera prioridad), con interés en iOS o al menos una PWA.
> Fase 2 (pendiente): mejoras de usabilidad para personas mayores.
>
> Fuentes principales: [imposter.app/es](https://imposter.app/es/), [guía de reglas](https://imposter.app/es/how-to-play-imposter-game/), [modo online](https://imposter.app/es/online/) y ~15 repositorios de GitHub (ver sección 3).
> Fecha de recopilación: 2026-08-25.

---

## 1. Qué es el juego y sus reglas (según imposter.app)

### 1.1 Concepto

El **juego del impostor** es un juego de fiesta de deducción social:

- La mayoría de los jugadores reciben la **misma palabra secreta** (ej. "Pizza").
- Un jugador (el **impostor**) **no recibe nada** (o solo una pista vaga de categoría, ej. "Comida").
- Todos se turnan dando **pistas de una sola palabra** relacionada con la palabra secreta.
- El impostor debe integrarse/blufear sin saber la palabra, e intentar adivinarla escuchando las pistas.
- Después de las pistas se **discute** y se **vota** quién es el impostor.

### 1.2 Qué necesitas

- **3+ jugadores** (óptimo 4–8; escalable a grupos grandes).
- **Un solo dispositivo** (móvil, tablet u ordenador) — se pasa de mano en mano.
- Un generador de palabras (o más de 100 palabras por categoría).

### 1.3 Fases de la partida

1. **Asignar roles**: un jugador inicia y pasa el dispositivo. Cada jugador ve su carta en secreto:
   - Jugadores normales: ven la palabra secreta (ej. "Pizza").
   - El impostor: ve "Eres el Impostor" (a veces con una pista de categoría, ej. "Comida").
   - Nadie revela lo que vio. El dispositivo se pasa hasta que todos hayan visto su carta.
2. **Dar pistas**: empezando por un jugador aleatorio, todos se turnan para dar **una pista de una palabra** relacionada con la secreta.
3. **Discusión**: tras una o dos rondas de pistas, se discute: pedir que expliquen sus pistas, señalar respuestas sospechosas, defenderse, formar teorías.
4. **Votar**: el grupo vota quién cree que es el impostor (señalando a la vez o uno a uno).

### 1.4 Reglas para las pistas (importantes)

- Debe ser **exactamente una palabra**.
- **No puede ser la palabra secreta**.
- **No puede ser una variación directa** (no "Pizzas" si la palabra es "Pizza").
- Debe **relacionarse** con la palabra de alguna manera.

### 1.5 Condiciones de victoria

- **El grupo gana** si vota correctamente y atrapa al impostor.
- **El impostor gana** si el grupo vota mal, si **adivina la palabra secreta** (algunas versiones le dan una última oportunidad de adivinarla incluso tras ser atrapado), o si se acaba el tiempo.
- En versiones multironda con eliminación (como la online de imposter.app): los civiles ganan al eliminar a todos los impostores; los impostores ganan si igualan o superan en número a los civiles.
- La partida termina cuando: el impostor adivina la palabra, el grupo lo identifica, o se agota el tiempo.

### 1.6 FAQ / reglas de casa (recogidas en la guía oficial)

- **Número de impostores**: 1 para 3–6 jugadores; 2 para 7+ (los impostores **no se conocen entre sí**).
- **Si alguien revela la palabra**: la mayoría reinicia la ronda o se ríe y continúa.
- **¿Puede el impostor hacer preguntas?**: sí, durante la discusión (es estrategia legítima).
- **Empate en la votación**: reglas de casa (revotación entre los empatados, o el empate cuenta como victoria del impostor).
- **Quién empieza**: cualquiera; la app puede elegir al azar.
- Una ronda típica dura **5–10 minutos**; se suelen jugar varias rondas rotando el impostor al azar.

### 1.7 Variaciones oficiales

- **Modo dibujo** (en lugar de hablar, dibujan).
- **Grupos grandes** (8+ jugadores).
- **Temas/categorías** (comida, películas, objetos, etc.).

---

## 2. La versión online de referencia (imposter.app)

Es una PWA (sin descarga, sin registro) con **dos modos**:

### 2.1 Modo local "pass & play" (un solo dispositivo)

Pantallas y opciones observadas en la página principal:

- **Configuración**: jugadores (mínimo 3), impostores (por defecto 1), pistas para el impostor (sí/no), categorías (selección múltiple), límite de tiempo (por defecto **3 minutos**), palabras personalizadas (crear categoría propia; mínimo 3 palabras).
- **Gestión de jugadores**: agregar, editar nombre, eliminar, reiniciar jugadores.
- **Revelación de roles**: "Toca para revelar" → "Siguiente jugador" (paso del dispositivo).
- **Juego**: temporizador visible (03:00), botón **Parar**, **Cerrar ronda**, luego "¿Quién es?" (votación) y **Revelar Impostor**.
- **Fin**: reiniciar o salir (con confirmación).

### 2.2 Modo online multijugador (/es/online/)

- Crear **sala privada** (solo con código) o **sala pública** (cualquiera puede unirse).
- **Código de 6 dígitos** para invitar; enlace directo.
- **3–20 jugadores** por sala; mínimo 3 para empezar.
- Turnos de pista de **una palabra con 20 segundos** de límite.
- **Votación por ronda**; las **faltas se acumulan** entre rondas y un jugador es eliminado al superar el límite.
- El impostor **puede ganar en cualquier momento escribiendo la palabra secreta** en su turno (victoria instantánea).
- **Chat** en la sala; mostrar país opcional; confirmación de edad (17+).
- **Sin registro ni anuncios**; el **servidor guarda la palabra secreta** para evitar trampas (inspección de código).
- En la sala se ven: ronda actual, palabra secreta (—), pistas de la ronda, "Es tu turno: da una pista", votación ("Vota por el impostor"), resultados, "La palabra secreta era…", revelación y fin de partida.

### 2.3 Datos de interés del ecosistema

- Tiene **app nativa iOS** ("Imposter" de Picoboom, 4.9★ con ~12K valoraciones según su web).
- Sitio con guías de contenido: 120+ palabras chistosas, 50+ difíciles, 100+ mejores palabras por categoría, 25 preguntas para descubrir al impostor, consejos, ideas de variaciones, modo dibujo, grupos grandes.
- Hay una app en Google Play ("Impostor", `com.ggonchapp.impostor`) cuyo autor afirma haber publicado el código open source (hallazgo en búsqueda, **no verificado en detalle**).

---

## 3. Repositorios en GitHub

### 3.1 Tabla resumen (búsqueda "impostor game/word/party" ordenada por estrellas)

| Repositorio | Stack | ⭐ | Demo | Notas |
|---|---|---|---|---|
| `KnotzerIO/find-the-impostor` | Next.js 15 + React 19 + TS + Tailwind 4 + Zustand + Dexie (IndexedDB) | 9 | impostor.knotzer.io | PWA offline-first, i18n EN/DE, MIT |
| `rickywong04/impostor` | JS + Tailwind; Node/Express + Socket.IO | 3 | imposterhunt.com | Pass&play + online (PIN), rol Jester, hard mode, Docker/Fly.io |
| `thgh/impostor.party` | Next.js (starter) | 3 | — | Plantilla sin desarrollo real |
| `Bastes/impostor` | Elixir | 1 | — | Backend Elixir (Phoenix) |
| `ritwikshanker/WordImpostor` | Kotlin | 1 | — | Android nativo |
| `retired64/ImpostorMX` | Flutter/Dart | 6 | — | Offline, sin anuncios, GPLv3, APK en releases |
| `oEnzoRibas/impostorsGame` | TS | 4 | — | PWA |
| `Stefano-Mazziotta/impostor-game` | Next.js | 1 | — | Starter sin features |
| `Adolfo-GM/impostor` | HTML/CSS/JS vanilla | 1 | adolfo-gm.github.io/impostor | Sin dependencias, hold-to-reveal, tiebreaker CPU |
| `Fvitu/Impostor-Game` | TS/React | 1 | impostor-game.fvitu.qzz.io | Online con Redis, cuentas, stats/leaderboard |
| `sanoopsamson77-png/imposter-party-game-IOS-` | Swift 5 + SwiftUI | 1 | — | **iOS nativo**, 3–24 jugadores, Mr. White |
| `einfachstarten/suswords` | Flask + Vanilla JS | 1 | impostor.pythonanywhere.com | Online con código/QR, PWA, MIT |
| `antebrl/undercover-word-game` | TS + WebRTC (PeerJS) | 18 | undercover.localplayer.dev | **Undercover** (variante: palabra similar + Mr. White) |
| `MASJV/undercover-game` | HTML | 1 | — | Variante undercover simple |
| `MyronFaryna/Undercover-react-game` | JS/React | 1 | — | Variante undercover |
| `tomas-tapia-a/Impostor-game-CIP2026` | Python | 1 | — | Proyecto académico |
| `ikeda7/x9-game` | TS | 1 | x9.dev.br | Juego similar |
| `JaraOps/Impostor_Game`, `Pankaj09997/Impostor-Game` (Dart), `Endika/flipper-impostor-game` (C, Flipper Zero), etc. | varios | 1 | — | Menores |

> Nota: la búsqueda "impostor" también devuelve muchos repos de **Among Us** (mods, detección de paquetes UDP, reimplementaciones) que **no** son el juego de palabras; se han descartado.

### 3.2 Detalle de los repositorios más relevantes

#### a) `Sergiolpz-dev/Impostor-juego` — referencia del usuario (repo base a estudiar)

- **Descripción**: "Juego del Impostor hecho con React, TW y PWA para móvil". Demo: https://impostor.sergiolpz.cloud/
- **Stack** (package.json): Vite 7, React 19.2, TypeScript 5.9, Tailwind 4.1 (plugin Vite), `vite-plugin-pwa` 1.2, Zod 3.22 (validación), Remotion 4 (video promocional). Sin router ni librería de estado.
- **Arquitectura** (confirmada leyendo el código):
  - `src/App.tsx` — máquina de estados con fases `setup → roleReveal → playing → ended`; barajado Fisher-Yates para asignar impostores; `crypto.randomUUID()` para IDs; condición de victoria: impostores ganan si `vivos_impostores >= vivos_civiles`, civiles ganan si no quedan impostores; jugador inicial aleatorio.
  - `src/components/`: `SetupScreen.tsx`, `RoleRevealScreen.tsx`, `GameScreen.tsx`, `EndGameScreen.tsx`.
  - `src/data/words.ts` — 60 KB de palabras, 11 categorías (Animales, Comida, Lugares, Objetos, Profesiones, Deportes, Películas/Series, Países, Conceptos, Naturaleza, Fantasía), "130+ palabras".
  - `src/types/game.ts` — tipos y estado inicial.
- **Jugabilidad**: modo local pass & play; jugadores ven su rol en privado; discusión con temporizador; votación con eliminación; victoria por mayoría de impostores vivos.
- **Licencia**: **sin licencia** → por defecto "todos los derechos reservados"; sirve como referencia de aprendizaje, pero **no copiar código directamente** sin permiso del autor.
- **Sobre el autor**: estudiante (según su perfil), proyecto con propósito educativo; UI sencilla — de ahí el interés del usuario en mejorarla.

#### b) `KnotzerIO/find-the-impostor` — la referencia PWA más cuidada

- Next.js 15.3 (App Router) + React 19 + TS 5 + Tailwind 4 + Radix UI + Zustand + Dexie (IndexedDB) + next-intl (EN/DE) + Umami + Sentry. **MIT**.
- 3–10 jugadores, 1–3 impostores, categorías propias, pistas opcionales para el impostor.
- 2 rondas de pistas de una palabra + discusión final + votación.
- Offline-first (IndexedDB), instalable. Es el referente de "cómo hacer bien una PWA local-first de este juego".

#### c) `antebrl/undercover-word-game` — variante Undercover con online sin servidor

- Web del juego **Undercover** (Yanstar Studio): 3 grupos — **Civiles** (misma palabra), **Undercover** (palabra *ligeramente distinta*), **Mr. White** (sin palabra).
- Online **peer-to-peer con WebRTC (PeerJS)** — sin servidor propio; fácil de autohospedar (npm, puerto 8080).
- Fases: descripción → discusión → eliminación (el eliminado con rol Mr. White puede adivinar la palabra y ganar al instante).
- Relevante como referencia de **online sin backend** y de la variante de roles múltiples.

#### d) `rickywong04/impostor` (impostorhunt.com) — referencia online con Socket.IO

- Frontend JS + Tailwind; backend Node/Express; **Socket.IO** tiempo real; Docker + Fly.io.
- Modos: **Pass to Play** y **Online multiplayer** (lobby con PIN).
- Twists opcionales: **rol Jester** (gana si le votan), **Hard Mode** (el impostor solo ve el tema), **Hint Word** (pista sutil al impostor).

#### e) `einfachstarten/suswords` — referencia de sala con código + QR (Python)

- Backend **Flask** con estado en archivos JSON; frontend vanilla JS; PWA; despliegue en PythonAnywhere.
- Crear sala → compartir **QR o código de 4 dígitos** → mínimo 3 jugadores → rondas de pistas → votación.
- MIT. Arquitectura muy simple (5 templates + JS estático) — útil para entender un MVP online completo.

#### f) `retired64/ImpostorMX` — referencia Android offline (Flutter)

- **Flutter 3.x / Dart**, GPLv3, 100 % offline, sin anuncios, APK distribuido por GitHub Releases (con badge de descargas y Ko-fi).
- Es la referencia más cercana a "app Android nativa offline" del juego (aunque vía Flutter, no Kotlin/Compose).

#### g) `sanoopsamson77-png/imposter-party-game-IOS-` — referencia iOS nativa (SwiftUI)

- Swift 5 + SwiftUI, iOS 15+, pass & play, **3–24 jugadores**.
- Roles: 1 Impostor + opcional Mr. White + Civiles; categorías extensibles (`WordList.swift`).
- Estructura clara: `GameViewModel.swift` (estado, votación, condiciones de victoria), `SetupView`, `RevealView`, `DiscussionView`, `VotingView`, `EliminationView`, `GameOverView`.
- Detalle útil: si el eliminado es Mr. White, puede adivinar la palabra para victoria instantánea; si quedan 2 jugadores, el Impostor gana.

#### h) `Fvitu/Impostor-Game` — referencia online "completa" (cuentas + Redis)

- 3–16 jugadores, pass & play o remoto con móviles.
- Sin cuentas obligatorias, sin temporizadores impuestos por servidor; **estado en Redis** (claves `impostor:game:*, impostor:user:*, impostor:stats:*, impostor:leaderboard:*`), stats por categoría y leaderboard.
- Interesante si en el futuro se quieren estadísticas/multijugador persistente.

#### i) `Adolfo-GM/impostor` — UX simple en vanilla JS

- HTML/CSS/JS sin dependencias, PWA, localStorage para nombres.
- Mecánica de revelado **hold-to-reveal** (mantener pulsado 0,7 s, ventana de 10 s) y **tiebreaker por CPU** en votos empatados; cuenta atrás visual con fondo rojo.
- Útil por sus micro-decisiones de UX (privacidad de la carta, desempates).

---

## 4. Análisis comparativo

### 4.1 Modos de juego

- **Pass & play (1 dispositivo)**: es el modo base de casi todos (Sergiolpz, find-the-impostor, Adolfo-GM, ImpostorMX, iOS SwiftUI, imposter.app local).
- **Online (varios dispositivos)**: imposter.app online (código 6 dígitos), imposterhunt (Socket.IO), suswords (Flask + código/QR), undercover-web (WebRTC P2P), Fvitu (Redis).
- El modo online añade requisitos de **backend** (o P2P) y problemas nuevos: anti-trampas (ocultar la palabra en servidor), reintentos de conexión, gestión de salas.

### 4.2 Roles y variantes

- **Básico**: Civiles (misma palabra) vs Impostor(es) (sin palabra, a veces con pista de categoría).
- **Mr. White / Blank**: un jugador sin palabra (Undercover, iOS SwiftUI) — con derecho a adivinar si le eliminan.
- **Undercover**: el impostor recibe una palabra *parecida* (variante china "谁是卧底").
- **Jester**: gana si le votan (impostorhunt).
- **Hard mode**: el impostor solo ve el tema (impostorhunt).
- **Pistas al impostor**: opción en casi todas las versiones (find-the-impostor, imposter.app, Adolfo-GM).

### 4.3 Arquitecturas observadas

- **PWA React (Vite)**: Sergiolpz (la más simple, sin router/estado), find-the-impostor (la más completa).
- **Next.js**: find-the-impostor, thgh, Stefano-Mazziotta.
- **Vanilla HTML/CSS/JS**: Adolfo-GM, suswords (frontend).
- **Backend**: Node/Express + Socket.IO (impostorhunt), Flask + JSON (suswords), Redis (Fvitu), WebRTC P2P (undercover), Elixir (Bastes).
- **Nativo**: Kotlin (WordImpostor), Flutter (ImpostorMX), SwiftUI (iOS), Dart (Pankaj09997).

### 4.4 Palabras y categorías

- Sergiolpz: 130+ palabras, 11 categorías en `words.ts` (60 KB) — buena base en español.
- find-the-impostor: categorías y **palabras propias**; i18n.
- imposter.app: cientos de palabras, docenas de categorías, **palabras personalizadas** (mínimo 3).
- Los repos en inglés necesitan traducción; la lista de Sergiolpz ya está en español.

### 4.5 Micro-UX relevantes (candidatas a heredar/mejorar)

- **Toca para revelar** vs **mantener pulsado** para ver la carta (Adolfo-GM) — privacidad.
- **Votación con desempate automático** (CPU) — evita bloqueos.
- **Confirmación antes de salir** (imposter.app) — evita pérdidas accidentales.
- **Persistencia de nombres** en localStorage (Adolfo-GM, Sergiolpz guarda jugadores/categoría).
- **Temporizador visual** (cuenta atrás con barra/relleno).
- **Eliminación por faltas** en online (imposter.app) — evita ronda eterna.
- **Victoria instantánea escribiendo la palabra** (imposter.app online).

### 4.6 Licencias (importante antes de copiar código)

- **MIT**: find-the-impostor, suswords.
- **GPLv3**: ImpostorMX (si se deriva, la obra debe ser GPL).
- **Sin licencia** (todos los derechos reservados por defecto): Sergiolpz, rickywong04, sanoopsamson, antebrl, Fvitu, Adolfo-GM (este último declara "uso personal/educativo").
- → Plan recomendado: **escribir código propio** tomando estos proyectos como referencia funcional/visual, y solo reutilizar lo que tenga licencia compatible (p. ej. listas de palabras propias, no copiadas).

---

## 5. Opciones tecnológicas para nuestra app

### 5.1 Android nativo — Kotlin + Jetpack Compose

- **Ventajas**: rendimiento y sensación nativa; accesibilidad de primer nivel (TalkBack, escala de fuente, zonas táctiles); APIs de texto a voz (TTS) y háptica; sin dependencia de navegador; distribución por APK/Play Store.
- **Desventajas**: solo Android (la versión iOS habría que hacerla aparte); más trabajo que una PWA.
- Encaja con el objetivo declarado ("app para android nativa") y con el entorno Termux disponible para compilar.

### 5.2 iOS nativo — SwiftUI

- Referencia directa: `sanoopsamson77-png/imposter-party-game-IOS-` (misma mecánica, pasos de pantalla mapeados).
- **Ventajas**: accesibilidad de Apple (Dynamic Type, VoiceOver), App Store.
- **Desventajas**: requiere Mac/Xcode para compilar y firmar; duplica el desarrollo.

### 5.3 PWA (React/Vite/Next)

- Referencias: Sergiolpz, find-the-impostor.
- **Ventajas**: una sola base para Android/iOS/escritorio; instalable; offline con service worker; despliegue barato (Vercel/Cloudflare).
- **Desventajas**: menos integración con accesibilidad del sistema (aunque soporta `prefers-reduced-motion`, escalado de fuente del navegador, etc.); el modo online necesita backend.

### 5.4 Flutter (opción intermedia)

- Referencia: ImpostorMX.
- Una base de código para Android+iOS con calidad casi nativa; buena accesibilidad; pero menos "nativa" que Kotlin/SwiftUI y con runtime propio.

### 5.5 Recomendación preliminar (a debatir)

1. **Fase 1 (MVP local, pass & play)**: Android nativo con Kotlin + Jetpack Compose, replicando el flujo de imposter.app (setup → revelación → pistas/temporizador → votación → fin). Lista de palabras propia en español (inspirada en categorías de Sergiolpz, escritas por nosotros).
2. **Online (fase posterior opcional)**: si se quiere multijugador a distancia, valorar un backend pequeño (Node/Socket.IO o similar) o WebRTC P2P.
3. **iOS**: o bien una app SwiftUI aparte, o bien una PWA compartida si se prioriza el alcance multiplataforma sobre lo nativo.
4. Las mejoras de accesibilidad para mayores (fase 2) son más fáciles de hacer bien en nativo (Compose/SwiftUI) por el soporte de accesibilidad del sistema.

---

## 6. Notas preliminares para la fase 2 (usabilidad personas mayores)

> Se desarrollará en profundidad en la fase 2, con pruebas reales. Puntos de partida detectados al comparar las versiones existentes:

- Tipografías pequeñas y bajo contraste en la mayoría de PWAs → modo de **texto grande**, paleta de alto contraste, fuente legible (tamaño configurable).
- Gestos finos (tap pequeño, mantener pulsado 0,7 s, deslizamientos) → **botones grandes** (≥ 48–64 dp), evitar hold-to-reveal o alargar la ventana, sin gestos complejos.
- Temporizadores estresantes (20 s por turno en online) → tiempos ampliados o sin límite en modo "relajado".
- Jerga confusa ("blufear", "pass & play") → lenguaje sencillo, iconos grandes, instrucciones paso a paso.
- Apoyo al impostor: pistas más claras o **lectura en voz alta (TTS)** de la palabra/categoría para personas con dificultad de lectura.
- Confirmaciones claras antes de acciones destructivas y "deshacer" (ya presente en imposter.app para salir).
- Tutorial guiado inicial y modo de práctica contra "oponentes ficticios" opcional.
- Reducir el número de pantallas/pasos entre "iniciar" y "jugar"; recordar configuración de la partida anterior.

---

## 7. Fuentes

- Reglas: https://imposter.app/es/how-to-play-imposter-game/
- Juego online: https://imposter.app/es/ y https://imposter.app/es/online/
- Repos GitHub (ver sección 3): Sergiolpz-dev/Impostor-juego; KnotzerIO/find-the-impostor; rickywong04/impostor; antebrl/undercover-word-game; einfachstarten/suswords; retired64/ImpostorMX; sanoopsamson77-png/imposter-party-game-IOS-; Fvitu/Impostor-Game; Adolfo-GM/impostor; thgh/impostor.party; Stefano-Mazziotta/impostor-game; MASJV/undercover-game; MyronFaryna/Undercover-react-game; tomas-tapia-a/Impostor-game-CIP2026; ikeda7/x9-game; Bastes/impostor; ritwikshanker/WordImpostor; oEnzoRibas/impostorsGame; Endika/flipper-impostor-game; JaraOps/Impostor_Game; Pankaj09997/Impostor-Game.
- Variante de referencia (juego físico): Undercover de Yanstar Studio (app Android) y "The Chameleon" (Big Potato) como juegos hermanos del género.
