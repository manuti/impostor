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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.impostor.game.R
import com.impostor.game.game.Player
import com.impostor.game.game.Role
import com.impostor.game.game.WordPair
import com.impostor.game.ui.components.PhaseTag
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
    // Punto 11: el foco inicial de los diálogos va a "Cancelar".
    val cancelAbandonFocus = remember { FocusRequester() }
    val cancelEliminateFocus = remember { FocusRequester() }

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
        Spacer(Modifier.height(8.dp))
        // Etiqueta de fase (Wikipedia: la discusión pasa a la "plaza pública")
        PhaseTag(stringResource(R.string.phase_public))
        Spacer(Modifier.height(8.dp))
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

        // Lista de jugadores con scroll propio (punto 9 + corrección v0.4.1):
        // al pasar la rejilla de 2 columnas a lista vertical, un grupo de 6+
        // jugadores desborda esta columna (que no tenía scroll) y empujaba los
        // contadores fuera de la pantalla. El scroll interno mantiene los
        // contadores anclados abajo y conserva la apariencia actual con grupos
        // pequeños (la lista ocupa el hueco libre y su contenido va arriba).
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            // Jugadores vivos: lista vertical uniforme; cada botón lleva el icono
            // de voto y el aviso de que son votables va justo encima (punto 9).
            Text(
                text = stringResource(R.string.game_alive, alivePlayers.size),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = gameColors.success,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.game_vote_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            alivePlayers.forEach { player ->
                val voteCd = stringResource(R.string.game_vote_player_cd, player.name)
                Button(
                    onClick = { selectedPlayer = player },
                    enabled = eliminatedPlayer == null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .semantics { contentDescription = voteCd },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_vote),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
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
                deadPlayers.forEach { player ->
                    val deadColor = if (player.role == Role.IMPOSTOR) gameColors.impostor else gameColors.civil
                    val roleName = stringResource(
                        if (player.role == Role.IMPOSTOR) R.string.game_role_impostor else R.string.game_role_civil
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
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
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

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
    }

    // Confirmación al abandonar
    if (showAbandonDialog) {
        LaunchedEffect(Unit) { cancelAbandonFocus.requestFocus() }
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
                TextButton(
                    onClick = { showAbandonDialog = false },
                    modifier = Modifier.focusRequester(cancelAbandonFocus),
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    // Diálogo de confirmación de eliminación
    selectedPlayer?.let { sel ->
        LaunchedEffect(sel) { cancelEliminateFocus.requestFocus() }
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
                TextButton(
                    onClick = { selectedPlayer = null },
                    modifier = Modifier.focusRequester(cancelEliminateFocus),
                ) {
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
