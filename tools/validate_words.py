#!/usr/bin/env python3
"""Valida los ficheros de contenido del juego (app/src/main/assets/words/*.json).

Uso:
    python3 tools/validate_words.py [directorio words]

Comprueba, para cada idioma:
  - JSON válido con el esquema esperado: locale + categories[{id, name, words[{word, hint}]}].
  - `locale` coherente con el nombre del fichero (es-ES.json -> "es-ES").
  - ids de categoría únicos, sin campos vacíos ni espacios sobrantes.
  - palabras no repetidas dentro del mismo idioma.

Y entre idiomas (comparando con el primero como referencia):
  - mismos ids de categoría, en el mismo orden y con el mismo número de palabras.

Sin dependencias externas (solo librería estándar). Devuelve 0 si todo es válido
y 1 si hay errores; los avisos no rompen la validación.

Pensado para CI: los PR de idiomas los revisa una persona autorizada (CODEOWNERS);
este script es la primera barrera automática.
"""

import json
import sys
from pathlib import Path

CATEGORY_KEYS = {"id", "name", "words"}
WORD_KEYS = {"word", "hint"}


def check_structure(path, data, errors):
    """Valida el esquema de un fichero y devuelve [(id, name, [palabras]), ...]."""
    if not isinstance(data, dict):
        errors.append(f"{path.name}: la raíz debe ser un objeto JSON")
        return []
    if set(data.keys()) != {"categories", "locale"}:
        errors.append(
            f"{path.name}: claves inesperadas {sorted(data.keys())}; "
            f"se esperan ['categories', 'locale']"
        )
    locale = data.get("locale")
    expected_locale = path.stem
    if locale != expected_locale:
        errors.append(
            f"{path.name}: \"locale\" es {locale!r} y debería ser {expected_locale!r} "
            "(debe coincidir con el nombre del fichero)"
        )
    categories = data.get("categories")
    if not isinstance(categories, list) or not categories:
        errors.append(f"{path.name}: \"categories\" debe ser una lista con al menos una categoría")
        return []

    result = []
    seen_ids = set()
    for index, category in enumerate(categories):
        if not isinstance(category, dict) or set(category.keys()) != CATEGORY_KEYS:
            errors.append(
                f"{path.name}: categoría #{index} debe tener exactamente {sorted(CATEGORY_KEYS)}"
            )
            continue
        category_id = category["id"]
        name = category["name"]
        words = category["words"]
        if not isinstance(category_id, str) or not category_id.strip():
            errors.append(f"{path.name}: categoría #{index} sin \"id\" válido")
            continue
        if category_id != category_id.strip().lower() or " " in category_id:
            errors.append(
                f"{path.name}: id {category_id!r} debe ser un slug en minúsculas sin espacios "
                "(p. ej. \"animals\")"
            )
        if category_id in seen_ids:
            errors.append(f"{path.name}: id de categoría repetido: {category_id!r}")
        seen_ids.add(category_id)
        if not isinstance(name, str) or not name.strip():
            errors.append(f"{path.name}: categoría {category_id!r} sin \"name\"")
        if name != name.strip():
            errors.append(f"{path.name}: el nombre de {category_id!r} tiene espacios sobrantes")
        if not isinstance(words, list) or not words:
            errors.append(f"{path.name}: categoría {category_id!r} sin palabras")
            continue

        cleaned = []
        for word_index, word in enumerate(words):
            if not isinstance(word, dict) or set(word.keys()) != WORD_KEYS:
                errors.append(
                    f"{path.name}: {category_id}[{word_index}] debe tener exactamente "
                    f"{sorted(WORD_KEYS)}"
                )
                continue
            text = word["word"]
            hint = word["hint"]
            if not isinstance(text, str) or not text.strip():
                errors.append(f"{path.name}: {category_id}[{word_index}] sin \"word\"")
                continue
            if not isinstance(hint, str) or not hint.strip():
                errors.append(f"{path.name}: {category_id}[{word_index}] sin \"hint\"")
                continue
            if text != text.strip() or hint != hint.strip():
                errors.append(
                    f"{path.name}: {category_id}[{word_index}] ({text!r}) tiene espacios sobrantes"
                )
            cleaned.append((text, hint))

        repeated = {text for text, _ in cleaned if [w for w, _ in cleaned].count(text) > 1}
        if repeated:
            errors.append(f"{path.name}: palabras repetidas en {category_id!r}: {sorted(repeated)}")
        result.append((category_id, name, cleaned))
    return result


def main():
    if len(sys.argv) > 1:
        words_dir = Path(sys.argv[1])
    else:
        words_dir = Path(__file__).resolve().parent.parent / "app/src/main/assets/words"

    if not words_dir.is_dir():
        print(f"ERROR: no existe el directorio {words_dir}")
        return 1

    files = sorted(words_dir.glob("*.json"))
    if not files:
        print(f"ERROR: no hay ficheros .json en {words_dir}")
        return 1

    errors = []
    warnings = []
    languages = {}
    for path in files:
        try:
            data = json.loads(path.read_text(encoding="utf-8"))
        except json.JSONDecodeError as exc:
            errors.append(f"{path.name}: JSON inválido ({exc})")
            continue
        languages[path.name] = check_structure(path, data, errors)

    reference_name = None
    for name, categories in languages.items():
        if categories and reference_name is None:
            reference_name = name
    if reference_name:
        reference = languages[reference_name]
        reference_ids = [category_id for category_id, _, _ in reference]
        for name, categories in languages.items():
            if name == reference_name or not categories:
                continue
            ids = [category_id for category_id, _, _ in categories]
            if ids != reference_ids:
                errors.append(
                    f"{name}: los ids de categoría no coinciden en orden con "
                    f"{reference_name}: {ids} != {reference_ids}"
                )
                continue
            for (ref_id, _, ref_words), (_, _, words) in zip(reference, categories):
                if len(ref_words) != len(words):
                    errors.append(
                        f"{name}: {ref_id} tiene {len(words)} palabras y "
                        f"{reference_name} tiene {len(ref_words)}"
                    )
            identical = all(
                [w for w, _ in ref_words] == [w for w, _ in words]
                for (_, _, ref_words), (_, _, words) in zip(reference, categories)
            )
            if identical:
                warnings.append(f"{name}: mismo contenido que {reference_name} (¿sin traducir?)")

    for name, categories in sorted(languages.items()):
        total = sum(len(words) for _, _, words in categories)
        print(f"{name}: {len(categories)} categorías, {total} palabras")

    for warning in warnings:
        print(f"AVISO: {warning}")
    for error in errors:
        print(f"ERROR: {error}")

    if errors:
        print(f"\n{len(errors)} error(es): contenido NO válido")
        return 1
    print("\nContenido válido.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
