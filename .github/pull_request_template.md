## Qué cambia

<!-- Describe el cambio en una o dos frases. -->

## Si el PR toca listas de palabras o traducciones (`app/src/main/assets/words/`)

- [ ] He ejecutado `python3 tools/validate_words.py` y pasa sin errores (`Contenido válido.`).
- [ ] El id de cada categoría es **slug estable en minúsculas** (`animals`, `food`, …) y **no** se ha traducido.
- [ ] El fichero nuevo se llama como su `locale` (p. ej. `pt-PT.json` → `"locale": "pt-PT"`) con etiqueta BCP-47.
- [ ] Todas las categorías tienen el **mismo número de palabras** que el idioma de referencia, en el mismo orden.
- [ ] He revisado la **ortografía y la terminología** a mano (las traducciones automáticas sin revisar no se aceptan).
- [ ] No he tocado la lógica del juego ni he añadido dependencias.

> Los PRs de idioma los revisa y autoriza el code owner (`@manuti`) antes de fusionarse.

## Comprobaciones generales

- [ ] Compila (`sh gradlew :app:assembleDebug`).
- [ ] Probado en dispositivo real.
