package com.impostor.game.ui.screens

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.impostor.game.game.Player
import com.impostor.game.game.Role
import com.impostor.game.game.WordPair
import kotlinx.coroutines.delay

private val Green = Color(0xFF4ADE80)
private val Red = Color(0xFFF87171)
private val Blue = Color(0xFF60A5FA)

/**
 * Pantalla de debate: votar y eliminar a un jugador (portada de la referencia).
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
                text = "Debate",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            TextButton(onClick = onAbandonGame) {
                Text("✕ Abandonar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(
            text = "Habla sobre tu palabra sin decirla. Encuentra al impostor.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Categoría: ${currentWord.category}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))

        // Jugadores vivos
        Text(
            text = "● Vivos (${alivePlayers.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Green,
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
                            .height(56.dp),
                    ) {
                        Text(
                            text = player.name,
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
                text = "● Eliminados (${deadPlayers.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Red,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            deadPlayers.chunked(2).forEach { chunk ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    chunk.forEach { player ->
                        val deadColor = if (player.role == Role.IMPOSTOR) Red else Blue
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(deadColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .border(1.dp, deadColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "${player.name} (${if (player.role == Role.IMPOSTOR) "Impostor" else "Civil"})",
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
                    color = Blue,
                )
                Text(
                    text = "Civiles vivos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${alivePlayers.count { it.role == Role.IMPOSTOR }}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Red,
                )
                Text(
                    text = "Impostores vivos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Pulsa en un jugador para votarlo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    // Diálogo de confirmación de eliminación
    selectedPlayer?.let { sel ->
        AlertDialog(
            onDismissRequest = { selectedPlayer = null },
            title = { Text("Eliminar jugador") },
            text = { Text("¿Estás seguro de que quieres eliminar a ${sel.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        eliminatedPlayer = sel
                        selectedPlayer = null
                        onEliminatePlayer(sel.id)
                    },
                ) {
                    Text("Eliminar", color = Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPlayer = null }) { Text("Cancelar") }
            },
        )
    }

    // Diálogo de resultado de la eliminación
    eliminatedPlayer?.let { elim ->
        val wasImpostor = elim.role == Role.IMPOSTOR
        AlertDialog(
            onDismissRequest = {},
            containerColor = if (wasImpostor) Color(0xFF7F1D1D) else Color(0xFF1E3A8A),
            title = {
                Text(
                    text = elim.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (wasImpostor) "Era IMPOSTOR" else "Era CIVIL",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (wasImpostor) Color(0xFFFCA5A5) else Color(0xFF93C5FD),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (wasImpostor) "¡Habéis eliminado a un impostor!" else "Era inocente...",
                        color = Color.White,
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
                        text = "Continuar",
                        color = Color.White,
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
                color = Color(0xFF4C1D95),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFA78BFA)),
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "🎯 Jugador inicial",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFC4B5FD),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = startingPlayer,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "¡Empieza tú!",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFC4B5FD),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Toca para cerrar",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}
