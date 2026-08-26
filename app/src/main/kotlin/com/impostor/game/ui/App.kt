package com.impostor.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.impostor.game.game.GameConfig
import com.impostor.game.game.GamePhase
import com.impostor.game.game.GameState
import com.impostor.game.game.Player
import com.impostor.game.game.Role
import com.impostor.game.game.getRandomWord
import com.impostor.game.ui.screens.EndGameScreen
import com.impostor.game.ui.screens.GameScreen
import com.impostor.game.ui.screens.RoleRevealScreen
import com.impostor.game.ui.screens.SetupScreen
import java.util.UUID

/**
 * Máquina de estados del juego, portada de la referencia React
 * (Sergiolpz-dev/Impostor-juego): setup -> roleReveal -> playing -> ended.
 */
@Composable
fun App() {
    var gameState by remember { mutableStateOf(GameState()) }
    var savedPlayerNames by remember { mutableStateOf(listOf<String>()) }
    var savedCategory by remember { mutableStateOf("Todas") }

    val startGame: (List<String>, GameConfig) -> Unit = { playerNames, config ->
        savedCategory = config.category ?: "Todas"
        savedPlayerNames = playerNames
        val word = getRandomWord(config.category)

        // Barajado Fisher-Yates para asignar impostores (misma mecánica que la referencia).
        val indices = playerNames.indices.toMutableList()
        for (i in indices.size - 1 downTo 1) {
            val j = (0..i).random()
            val tmp = indices[i]
            indices[i] = indices[j]
            indices[j] = tmp
        }
        val impostorIndices = indices.take(config.impostorCount).toSet()

        val players: List<Player> = playerNames.mapIndexed { index, name ->
            Player(
                id = UUID.randomUUID().toString(),
                name = name,
                role = if (index in impostorIndices) Role.IMPOSTOR else Role.CIVILIAN,
            )
        }

        gameState = GameState(
            phase = GamePhase.ROLE_REVEAL,
            players = players,
            config = config,
            currentWord = word,
            currentPlayerIndex = 0,
        )
    }

    val playerSawRole: (Int) -> Unit = { index ->
        gameState = gameState.copy(
            players = gameState.players.mapIndexed { i, p ->
                if (i == index) p.copy(hasSeenRole = true) else p
            },
            currentPlayerIndex = index + 1,
        )
    }

    val allPlayersSawRoles: () -> Unit = {
        gameState = gameState.copy(
            phase = GamePhase.PLAYING,
            startingPlayer = gameState.players.random().name,
        )
    }

    val eliminatePlayer: (String) -> Unit = { playerId ->
        val updated = gameState.players.map { p ->
            if (p.id == playerId) p.copy(isAlive = false) else p
        }
        val alive = updated.filter { it.isAlive }
        val aliveImpostors = alive.count { it.role == Role.IMPOSTOR }
        val aliveCivilians = alive.count { it.role == Role.CIVILIAN }
        val winner = when {
            aliveImpostors >= aliveCivilians -> Role.IMPOSTOR
            aliveImpostors == 0 -> Role.CIVILIAN
            else -> null
        }
        gameState = gameState.copy(
            players = updated,
            winner = winner,
            phase = if (winner != null) GamePhase.ENDED else gameState.phase,
        )
    }

    val resetGame: () -> Unit = { gameState = GameState() }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        when (gameState.phase) {
            GamePhase.SETUP -> SetupScreen(
                initialPlayerNames = savedPlayerNames,
                initialCategory = savedCategory,
                onStartGame = startGame,
            )

            GamePhase.ROLE_REVEAL -> RoleRevealScreen(
                players = gameState.players,
                currentPlayerIndex = gameState.currentPlayerIndex,
                currentWord = gameState.currentWord!!,
                config = gameState.config,
                onPlayerSawRole = playerSawRole,
                onAllPlayersSawRoles = allPlayersSawRoles,
                onAbandonGame = resetGame,
            )

            GamePhase.PLAYING -> GameScreen(
                players = gameState.players,
                currentWord = gameState.currentWord!!,
                startingPlayer = gameState.startingPlayer,
                onEliminatePlayer = eliminatePlayer,
                onAbandonGame = resetGame,
            )

            GamePhase.ENDED -> EndGameScreen(
                winner = gameState.winner!!,
                players = gameState.players,
                currentWord = gameState.currentWord!!,
                onPlayAgain = resetGame,
            )
        }
    }
}
