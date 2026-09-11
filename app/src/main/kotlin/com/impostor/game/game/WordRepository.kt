package com.impostor.game.game

import android.content.Context
import org.json.JSONObject

/**
 * Categoría de contenido: `id` estable (igual en todos los idiomas) y `name`
 * ya localizado para mostrar en la UI.
 */
data class WordCategory(val id: String, val name: String, val words: List<WordPair>)

/**
 * Carga el contenido del juego desde `assets/words/<locale>.json` (D-F3-1/D-F3-3).
 *
 * El contenido vive en JSON para que la comunidad pueda aportar idiomas con un
 * PR sin tocar Kotlin. El texto mostrado se separa del id estable: la lógica
 * (filtrado por categoría, C-1) usa siempre el id, nunca el nombre visible.
 *
 * El idioma activo lo fija AppCompatDelegate (selector in-app) o el sistema;
 * español (`es*`) → es-ES.json, cualquier otro → en-GB.json (fallback inglés).
 */
object WordRepository {

    /** Pseudocategoría "Todas": no está en el JSON, se genera en código. */
    const val ALL_ID = "all"

    private const val LOCALE_ES = "es-ES"
    private const val LOCALE_EN = "en-GB"
    private const val ASSET_ES = "words/es-ES.json"
    private const val ASSET_EN = "words/en-GB.json"

    private var cachedLocale: String? = null
    private var cachedCategories: List<WordCategory> = emptyList()

    /** Categorías del idioma activo, en el orden del JSON. */
    @Synchronized
    fun categories(context: Context): List<WordCategory> {
        val appContext = context.applicationContext
        val isSpanish = appContext.resources.configuration.locales[0].language == "es"
        val locale = if (isSpanish) LOCALE_ES else LOCALE_EN
        if (cachedLocale == locale && cachedCategories.isNotEmpty()) return cachedCategories
        val json = appContext.assets.open(if (isSpanish) ASSET_ES else ASSET_EN)
            .bufferedReader()
            .use { it.readText() }
        val loaded = parse(json)
        cachedLocale = locale
        cachedCategories = loaded
        return loaded
    }

    /** Pares (id, nombre localizado) ordenados por nombre, para el selector de categoría. */
    fun choices(context: Context): List<Pair<String, String>> =
        categories(context).map { it.id to it.name }.sortedBy { it.second }

    private fun parse(json: String): List<WordCategory> {
        val categories = JSONObject(json).getJSONArray("categories")
        return (0 until categories.length()).map { index ->
            val category = categories.getJSONObject(index)
            val name = category.getString("name")
            val words = category.getJSONArray("words")
            WordCategory(
                id = category.getString("id"),
                name = name,
                words = (0 until words.length()).map { wordIndex ->
                    val word = words.getJSONObject(wordIndex)
                    WordPair(
                        word = word.getString("word"),
                        hint = word.getString("hint"),
                        category = name,
                    )
                },
            )
        }
    }

    private fun pool(context: Context, categoryId: String?): List<WordPair> {
        val all = categories(context)
        val selected = categoryId
            ?.takeIf { it.isNotBlank() && it != ALL_ID }
            ?.let { wanted -> all.firstOrNull { it.id == wanted } }
        return selected?.words ?: all.flatMap { it.words }
    }

    /**
     * Palabra aleatoria, filtrando por categoría si se indica (id estable).
     *
     * C-1: `exclude` evita repetir la palabra de la partida anterior; solo se
     * aplica si el pool conserva más opciones (con una única palabra se permite
     * repetir, igual que antes de la migración a JSON).
     */
    fun randomWord(context: Context, categoryId: String? = null, exclude: WordPair? = null): WordPair {
        val words = pool(context, categoryId).ifEmpty { categories(context).flatMap { it.words } }
        val candidates = if (exclude == null) words else words.filter { it != exclude }
        return candidates.ifEmpty { words }.random()
    }
}
