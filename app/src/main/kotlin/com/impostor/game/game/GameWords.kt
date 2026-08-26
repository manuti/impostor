package com.impostor.game.game

/**
 * Lista de palabras propia del proyecto (redactada originalmente para esta app).
 * Formato: palabra + pista breve (para el impostor) + categoría.
 * Extensible: añade más entradas o categorías libremente.
 */
val wordList: List<WordPair> = listOf(
    // Animales
    WordPair("León", "Rey de la selva africana", "Animales"),
    WordPair("Delfín", "Salta y juega en el mar", "Animales"),
    WordPair("Elefante", "Trompa y orejas enormes", "Animales"),
    WordPair("Águila", "Ave rapaz de vista increíble", "Animales"),
    WordPair("Serpiente", "Se desliza sin patas", "Animales"),
    WordPair("Tortuga", "Lleva su casa a cuestas", "Animales"),
    WordPair("Pingüino", "No vuela, pero nada genial", "Animales"),
    WordPair("Canguro", "Salta con una bolsa", "Animales"),
    WordPair("Búho", "Despierto cuando anochece", "Animales"),
    WordPair("Jirafa", "El cuello más largo", "Animales"),

    // Comida
    WordPair("Paella", "Arroz con marisco, de Valencia", "Comida"),
    WordPair("Tacos", "Tortilla con relleno, de México", "Comida"),
    WordPair("Pizza", "Masa redonda con queso", "Comida"),
    WordPair("Chocolate", "Dulce hecho de cacao", "Comida"),
    WordPair("Gazpacho", "Sopa fría de tomate", "Comida"),
    WordPair("Tortilla de patatas", "Huevo y patata, tapa típica", "Comida"),
    WordPair("Jamón ibérico", "Curado y de bellota", "Comida"),
    WordPair("Churros", "Masa frita con azúcar", "Comida"),
    WordPair("Ensalada", "Lechuga, tomate y aliño", "Comida"),
    WordPair("Flan", "Postre de huevo con caramelo", "Comida"),

    // Lugares
    WordPair("Biblioteca", "Lugar lleno de libros", "Lugares"),
    WordPair("Estadio", "Donde se juega el fútbol", "Lugares"),
    WordPair("Mercado", "Fruta, verdura y puestos", "Lugares"),
    WordPair("Playa", "Arena, olas y sombrilla", "Lugares"),
    WordPair("Hospital", "Curan a los enfermos", "Lugares"),
    WordPair("Estación de tren", "Andenes y vías", "Lugares"),
    WordPair("Museo", "Cuadros y esculturas antiguas", "Lugares"),
    WordPair("Teatro", "Obras y funciones", "Lugares"),
    WordPair("Parque", "Árboles, bancos y columpios", "Lugares"),
    WordPair("Farmacia", "Medicinas y recetas", "Lugares"),

    // Objetos
    WordPair("Paraguas", "Se abre cuando llueve", "Objetos"),
    WordPair("Reloj", "Mide el tiempo", "Objetos"),
    WordPair("Linterna", "Ilumina en la oscuridad", "Objetos"),
    WordPair("Mochila", "Se lleva a la espalda", "Objetos"),
    WordPair("Tijeras", "Cortan papel y tela", "Objetos"),
    WordPair("Televisor", "Programas y series", "Objetos"),
    WordPair("Llaves", "Abre puertas", "Objetos"),
    WordPair("Espejo", "Devuelve tu imagen", "Objetos"),
    WordPair("Cepillo de dientes", "Higiene bucal diaria", "Objetos"),
    WordPair("Globo terráqueo", "Mapa esférico del mundo", "Objetos"),

    // Profesiones
    WordPair("Médico", "Cura enfermedades", "Profesiones"),
    WordPair("Bombero", "Apaga incendios", "Profesiones"),
    WordPair("Profesor", "Enseña en clase", "Profesiones"),
    WordPair("Panadero", "Hace pan y bollos", "Profesiones"),
    WordPair("Policía", "Mantiene el orden", "Profesiones"),
    WordPair("Peluquero", "Corta y peina el pelo", "Profesiones"),
    WordPair("Agricultor", "Cultiva el campo", "Profesiones"),
    WordPair("Camarero", "Sirve en restaurantes", "Profesiones"),
    WordPair("Mecánico", "Arregla coches", "Profesiones"),
    WordPair("Cartero", "Reparte cartas y paquetes", "Profesiones"),

    // Deportes
    WordPair("Fútbol", "Once contra once, balón a portería", "Deportes"),
    WordPair("Baloncesto", "Canasta y aro alto", "Deportes"),
    WordPair("Tenis", "Raqueta y red", "Deportes"),
    WordPair("Natación", "Crol, braza y piscina", "Deportes"),
    WordPair("Ciclismo", "Bicicleta de carretera", "Deportes"),
    WordPair("Atletismo", "Correr, saltar y lanzar", "Deportes"),
    WordPair("Boxeo", "Guantes y ring", "Deportes"),
    WordPair("Esquí", "Nieve, pendientes y bastones", "Deportes"),
    WordPair("Voleibol", "Red alta y remate", "Deportes"),
    WordPair("Gimnasia", "Ejercicios y aparatos", "Deportes"),

    // Países
    WordPair("España", "Flamenco y siesta, en Europa", "Países"),
    WordPair("México", "Mariachi y tacos", "Países"),
    WordPair("Japón", "Sushi y monte Fuji", "Países"),
    WordPair("Argentina", "Tango y asado", "Países"),
    WordPair("Italia", "Pasta y Coliseo", "Países"),
    WordPair("Francia", "Torre Eiffel y baguette", "Países"),
    WordPair("Brasil", "Samba y carnaval", "Países"),
    WordPair("Egipto", "Pirámides y río Nilo", "Países"),
    WordPair("Australia", "Canguros y playas", "Países"),
    WordPair("India", "Taj Mahal y curry", "Países"),

    // Naturaleza
    WordPair("Volcán", "Montaña que escupe lava", "Naturaleza"),
    WordPair("Río", "Agua que fluye hacia el mar", "Naturaleza"),
    WordPair("Bosque", "Muchos árboles juntos", "Naturaleza"),
    WordPair("Desierto", "Arena y calor extremo", "Naturaleza"),
    WordPair("Cascada", "Agua que cae desde lo alto", "Naturaleza"),
    WordPair("Luna", "Brilla por la noche", "Naturaleza"),
    WordPair("Arcoíris", "Siete colores tras la lluvia", "Naturaleza"),
    WordPair("Tormenta", "Rayos y truenos", "Naturaleza"),
    WordPair("Estrella", "Punto brillante en el cielo", "Naturaleza"),
    WordPair("Montaña", "Cima alta y nevada", "Naturaleza"),

    // Conceptos
    WordPair("Amistad", "Confianza entre personas", "Conceptos"),
    WordPair("Suerte", "Tener fortuna", "Conceptos"),
    WordPair("Silencio", "Ausencia de sonidos", "Conceptos"),
    WordPair("Libertad", "Poder elegir", "Conceptos"),
    WordPair("Tiempo", "Minutos y horas", "Conceptos"),
    WordPair("Memoria", "Recordar el pasado", "Conceptos"),
    WordPair("Miedo", "Susto intenso", "Conceptos"),
    WordPair("Alegría", "Sentirse muy feliz", "Conceptos"),
    WordPair("Paciencia", "Esperar sin enfadarse", "Conceptos"),
    WordPair("Sorpresa", "Algo inesperado", "Conceptos"),

    // Fantasía
    WordPair("Dragón", "Escupe fuego y vuela", "Fantasía"),
    WordPair("Hada", "Alas y varita mágica", "Fantasía"),
    WordPair("Unicornio", "Cuerno en la frente", "Fantasía"),
    WordPair("Mago", "Varita y hechizos", "Fantasía"),
    WordPair("Sirena", "Mitad mujer, mitad pez", "Fantasía"),
    WordPair("Bruja", "Escoba y pócimas", "Fantasía"),
    WordPair("Ogro", "Grande, verde y gruñón", "Fantasía"),
    WordPair("Elfo", "Orejas puntiagudas", "Fantasía"),
    WordPair("Troll", "Vive bajo el puente", "Fantasía"),
    WordPair("Fantasma", "Atraviesa las paredes", "Fantasía"),

    // Películas y series
    WordPair("Titanic", "El barco que se hunde", "Películas"),
    WordPair("King Kong", "Gorila gigante en rascacielos", "Películas"),
    WordPair("Parque Jurásico", "Dinosaurios en un parque", "Películas"),
    WordPair("Star Wars", "La fuerza y sables láser", "Películas"),
    WordPair("Harry Potter", "Niño mago con gafas", "Películas"),
    WordPair("El Rey León", "Simba y la sabana", "Películas"),
    WordPair("Indiana Jones", "Arqueólogo con látigo", "Películas"),
    WordPair("E.T.", "Extraterrestre que quiere volver", "Películas"),
    WordPair("Los Simpson", "La familia amarilla", "Películas"),
    WordPair("Matrix", "Neo y las pastillas", "Películas"),
)

/** Categorías disponibles, con "Todas" como opción por defecto. */
fun getAllCategories(): List<String> =
    listOf("Todas") + wordList.map { it.category }.distinct().sorted()

/** Devuelve una palabra aleatoria, filtrando por categoría si se indica. */
fun getRandomWord(category: String? = null): WordPair {
    val pool = if (category.isNullOrBlank() || category == "Todas") {
        wordList
    } else {
        wordList.filter { it.category == category }
    }
    return pool.random()
}
