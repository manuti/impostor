# Prompt de arranque — Fase 3 (El Impostor)

Pega el bloque siguiente como primer mensaje de la nueva sesión:

---

Fase 3 — El Impostor (i18n / internacionalización, v0.4.0).

1. **Lee `docs/PLAN-FASE3.md` completo antes de tocar código.** Es el plan de la fase 3 (i18n) y contiene: objetivo, reglas vinculantes (§4), trabajo previsto (§3) y decisiones pendientes (§5). **Aviso**: la §2 describe el estado ANTES de la fase 2 (2026-08-26, con ~62 cadenas hardcodeadas); las fases 2 y 2.5 cumplieron las reglas y dejaron la app **i18n-ready** (todas las cadenas en `res/values/strings.xml`, es-419). Al empezar, **verifica el estado real con un grep** (`grep -rn '"[^"]*[A-Za-zÁÉÍÓÚáéíóú]' app/src/main/kotlin --include='*.kt'` o revisión de pantallas) y actualiza la §2 del plan con lo que encuentres.
2. Verifica con `git status` / `git log` que estás en la rama `main`, con el árbol limpio y en el commit esperado (`b27f1e1` "PLAN-FASE4: tarea post-publicacion T-1…") antes de empezar. Estado de referencia: releases publicados v0.1.0…v0.3.4; última fase cerrada: 2.5 (mejoras intermedias).
3. **Solicita los permisos necesarios ahora** para intervención mínima: escritura en `app/src/` y `app/src/main/res/`, `bash` para compilar (`sh gradlew :app:assembleDebug`), copia del APK a `~/storage/downloads/` **nombrado `impostor-vX.Y.Z.apk`** (p. ej. `impostor-v0.4.0.apk`), y al final red para push/release.
4. **Antes de implementar, confirma con el usuario las 4 decisiones pendientes del plan (§5, D-F3-1…D-F3-4)**: (1) ¿traducir también el contenido (110 palabras + categorías) o solo la UI? (2) ¿idioma por sistema o selector in-app? (3) ¿locales iniciales (¿solo en, o también pt/fr…)? (4) ¿soporte RTL desde el inicio? Sin esa confirmación no toques código de la fase 3.
5. Reglas vinculantes (PLAN-FASE3 §4, heredadas y vigentes): todo texto en `res/values/strings.xml` (es-419 default) con `stringResource()`; placeholders `%1$s`/`%1$d`; `<plurals>` para conteos; tamaños en sp; `start/end` en vez de `left/right`; sin gramática española en lógica; contenido del juego separado de la UI (`GameWords.kt`); ortografía es-419 correcta — **"píldora", nunca "pillula"** (regla 8). Además: no añadir analytics/telemetría (fase 4), modelos como datos puros, TTS **solo** para textos no secretos — nunca la palabra/pista (la tarea T-1 del PLAN-FASE4 §7 es post-publicación y NO se implementa en esta fase).
6. Trabajo de la fase 3 (según decisiones confirmadas): completar la externalización de restos si el grep los encuentra → crear `values-en/` (y locales acordados) → mecanismo de idioma (sistema o selector in-app) → localizar contenido si se decide → `<plurals>`/placeholders donde falten → RTL si aplica. Al final: **v0.4.0** (versionCode +1), commit + push + release con `gh` y su APK `impostor-v0.4.0.apk`.
7. Ciclo por cambio: implementar → compilar → copiar el APK a Descargas → esperar confirmación del usuario para probar en el dispositivo real → solo con confirmación: commit + push + release. No toques la mecánica del juego ni añadas dependencias sin aprobación explícita.
8. Al cerrar la fase 3: actualiza `README.md` y añade las lecciones de la fase 3 al `docs/PLAN-FASE4.md` (incluida la revisión de la regla TTS "nunca contenido secreto" de cara a la tarea T-1 post-publicación).

---
