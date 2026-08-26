package com.impostor.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.impostor.game.game.GameConfig
import com.impostor.game.game.Player
import com.impostor.game.game.Role
import com.impostor.game.game.WordPair
import kotlinx.coroutines.delay

/**
 * Reparto privado de roles: cada jugador mantiene pulsado el círculo
 * para ver su rol (misma mecánica que la referencia React).
 */
@Composable
fun RoleRevealScreen(
    players: List<Player>,
    currentPlayerIndex: Int,
    currentWord: WordPair,
    config: GameConfig,
    onPlayerSawRole: (Int) -> Unit,
    onAllPlayersSawRoles: () -> Unit,
    onAbandonGame: () -> Unit,
) {
    val currentPlayer = players[currentPlayerIndex]
    val isLastPlayer = currentPlayerIndex == players.size - 1

    var held by remember(currentPlayerIndex) { mutableStateOf(false) }
    var revealing by remember(currentPlayerIndex) { mutableStateOf(false) }
    var seen by remember(currentPlayerIndex) { mutableStateOf(false) }

    // Mantener pulsado 300 ms para revelar el rol.
    LaunchedEffect(held) {
        if (held) {
            delay(300)
            revealing = true
        } else {
            revealing = false
        }
    }

    val isImpostor = currentPlayer.role == Role.IMPOSTOR
    val roleColor = if (isImpostor) Color(0xFFF87171) else Color(0xFF60A5FA)
    val roleText = if (isImpostor) "Eres IMPOSTOR" else "Eres CIVIL"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Abandonar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onAbandonGame) {
                Text("✕ Abandonar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Text(
            text = "Jugador ${currentPlayerIndex + 1} de ${players.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))

        // Puntos de progreso
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            players.forEachIndexed { index, _ ->
                val dotColor = when {
                    index < currentPlayerIndex -> Color(0xFF4ADE80)
                    index == currentPlayerIndex -> Color.White
                    else -> Color.White.copy(alpha = 0.3f)
                }
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
        }
        Spacer(Modifier.height(28.dp))

        Text(
            text = currentPlayer.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(28.dp))

        // Círculo de revelación
        val borderColor = if (revealing) roleColor else Color.White.copy(alpha = 0.3f)
        val bgColor = if (revealing) roleColor.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)

        Box(
            modifier = Modifier
                .size(192.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(4.dp, borderColor, CircleShape)
                .pointerInput(currentPlayerIndex) {
                    awaitEachGesture {
                        awaitFirstDown()
                        held = true
                        waitForUpOrCancellation()
                        held = false
                        if (revealing) {
                            seen = true
                            revealing = false
                        }
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            when {
                revealing -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp),
                ) {
                    Text(
                        text = roleText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = roleColor,
                    )
                    Spacer(Modifier.height(8.dp))
                    if (isImpostor) {
                        if (config.showHintToImpostor) {
                            Text(
                                text = "Tu pista es:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = currentWord.hint,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                            )
                        }
                    } else {
                        Text(
                            text = "Tu palabra es:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = currentWord.word,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                seen -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "✔",
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        color = Color(0xFF4ADE80),
                    )
                }

                else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🔒",
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Mantén pulsado",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f),
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        if (!seen) {
            Text(
                text = "Mantén pulsado el círculo para ver tu rol.\nSuéltalo cuando termines de leer.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                text = "Has visto tu rol",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4ADE80),
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                if (isLastPlayer) onAllPlayersSawRoles() else onPlayerSawRole(currentPlayerIndex)
            },
            enabled = seen,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(
                text = if (isLastPlayer) "Empezar Partida" else "Siguiente Jugador",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        if (!seen) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Debes ver tu rol antes de continuar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
