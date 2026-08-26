package com.impostor.game.game

/** Fases de la partida (máquina de estados, portada de la referencia React). */
enum class GamePhase { SETUP, ROLE_REVEAL, PLAYING, ENDED }

enum class Role { CIVILIAN, IMPOSTOR }

data class Player(
    val id: String,
    val name: String,
    val role: Role,
    val isAlive: Boolean = true,
    val hasSeenRole: Boolean = false,
)

data class WordPair(
    val word: String,
    val hint: String,
    val category: String,
)

data class GameConfig(
    val impostorCount: Int = 1,
    val showHintToImpostor: Boolean = true,
    val category: String? = null,
)

data class GameState(
    val phase: GamePhase = GamePhase.SETUP,
    val players: List<Player> = emptyList(),
    val config: GameConfig = GameConfig(),
    val currentWord: WordPair? = null,
    val currentPlayerIndex: Int = 0,
    val winner: Role? = null,
    val startingPlayer: String? = null,
)
