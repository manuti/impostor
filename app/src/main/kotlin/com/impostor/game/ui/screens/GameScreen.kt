package com.impostor.game.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.impostor.game.R
import com.impostor.game.game.Player
import com.impostor.game.game.Role
import com.impostor.game.game.WordPair
import com.impostor.game.ui.components.ThemeToggleButton
import com.impostor.game.ui.theme.gameColors
import kotlinx.coroutines.delay

/**
 * Pantalla de debate: votar y eliminar a un jugador (portada de la referencia).
 * Fase 2: strings externalizados, confirmación al salir, tokens de color y objetivos grandes.
 */
@Composable
fun GameScreen(
    players: List<Player>,
    currentWord: WordPair,
    startingPlayer: String?,
    onEliminatePlayer: (String) -> Unit,
    onAbandonGame: () -> Unit,
) {
    var selectedPlayer by remember { mutableStateOf<Player?>(null) }
    var eliminatedPlayer by remember { mutableStateOf<Player?>(null) }
    var showStartingPlayer by remember { mutableStateOf(true) }
    var showAbandonDialog by remember { mutableStateOf(false) }
    val gameColors = MaterialTheme.gameColors

    // Auto-cerrar el aviso del jugador inicial tras 3 segundos.
    LaunchedEffect(Unit) {
        delay(3000)
        showStartingPlayer = false
    }

    val alivePlayers = players.filter { it.isAlive }
    val deadPlayers = players.filter { !it.isAlive }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.game_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { showAbandonDialog = true }, modifier = Modifier.height(48.dp)) {
                    Text(
                        text = stringResource(R.string.action_leave),
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                ThemeToggleButton()
            }
        }
        Text(
            text = stringResource(R.string.game_instructions),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.game_category, currentWord.category),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))

        // Jugadores vivos
        Text(
            text = stringResource(R.string.game_alive, alivePlayers.size),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = gameColors.success,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        alivePlayers.chunked(2).forEach { chunk ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                chunk.forEach { player ->
                    Button(
                        onClick = { selectedPlayer = player },
                        enabled = eliminatedPlayer == null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp),
                    ) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                if (chunk.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
        }

        // Jugadores eliminados
        if (deadPlayers.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.game_dead, deadPlayers.size),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = gameColors.impostor,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            deadPlayers.chunked(2).forEach { chunk ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    chunk.forEach { player ->
                        val deadColor = if (player.role == Role.IMPOSTOR) gameColors.impostor else gameColors.civil
                        val roleName = stringResource(
                            if (player.role == Role.IMPOSTOR) R.string.game_role_impostor else R.string.game_role_civil
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .background(deadColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .border(1.dp, deadColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.game_player_with_role, player.name, roleName),
                                style = MaterialTheme.typography.bodyMedium,
                                color = deadColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    if (chunk.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.weight(1f))

        // Estadísticas
        Row(
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${alivePlayers.count { it.role == Role.CIVILIAN }}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = gameColors.civil,
                )
                Text(
                    text = stringResource(R.string.game_civilians_alive),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${alivePlayers.count { it.role == Role.IMPOSTOR }}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = gameColors.impostor,
                )
                Text(
                    text = stringResource(R.string.game_impostors_alive),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.game_vote_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    // Confirmación al abandonar
    if (showAbandonDialog) {
        AlertDialog(
            onDismissRequest = { showAbandonDialog = false },
            title = { Text(stringResource(R.string.game_abandon_title)) },
            text = { Text(stringResource(R.string.game_abandon_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAbandonDialog = false
                        onAbandonGame()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.action_abandon),
                        color = gameColors.impostor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showAbandonDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    // Diálogo de confirmación de eliminación
    selectedPlayer?.let { sel ->
        AlertDialog(
            onDismissRequest = { selectedPlayer = null },
            title = { Text(stringResource(R.string.game_eliminate_title)) },
            text = { Text(stringResource(R.string.game_eliminate_text, sel.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        eliminatedPlayer = sel
                        selectedPlayer = null
                        onEliminatePlayer(sel.id)
                    },
                ) {
                    Text(
                        text = stringResource(R.string.game_eliminate),
                        color = gameColors.impostor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPlayer = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    // Diálogo de resultado de la eliminación
    eliminatedPlayer?.let { elim ->
        val wasImpostor = elim.role == Role.IMPOSTOR
        AlertDialog(
            onDismissRequest = {},
            containerColor = if (wasImpostor) gameColors.impostorBg else gameColors.civilBg,
            title = {
                Text(
                    text = elim.name,
                    color = if (wasImpostor) gameColors.onImpostorBg else gameColors.onCivilBg,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(if (wasImpostor) R.string.game_was_impostor else R.string.game_was_civil),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (wasImpostor) gameColors.onImpostorBg else gameColors.onCivilBg,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(if (wasImpostor) R.string.game_impostor_found else R.string.game_innocent),
                        color = if (wasImpostor) gameColors.onImpostorBg else gameColors.onCivilBg,
                        textAlign = TextAlign.Center,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { eliminatedPlayer = null },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.game_continue),
                        color = if (wasImpostor) gameColors.onImpostorBg else gameColors.onCivilBg,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            },
        )
    }

    // Aviso del jugador inicial
    if (showStartingPlayer && startingPlayer != null) {
        Dialog(onDismissRequest = { showStartingPlayer = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.game_starting_player_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = startingPlayer,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.game_starting_player_you),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.game_starting_player_close),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}
