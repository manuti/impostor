package com.impostor.game.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.impostor.game.R
import com.impostor.game.game.GameConfig
import com.impostor.game.game.Player
import com.impostor.game.game.Role
import com.impostor.game.game.WordPair
import com.impostor.game.ui.components.MotionDetector
import com.impostor.game.ui.components.ThemeToggleButton
import com.impostor.game.ui.theme.gameColors
import kotlinx.coroutines.launch

/**
 * Reparto privado de roles (fase 2): el rol se revela con una animación
 * de naipe que gira sobre su eje vertical (requisito §3.14).
 * Anverso: logo del ojo. Reverso: palabra (civil) o pista/rol (impostor).
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
    val gameColors = MaterialTheme.gameColors

    var revealed by remember(currentPlayerIndex) { mutableStateOf(false) }
    var cardHiddenByShake by remember(currentPlayerIndex) { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    // BUG-1: la rotación se resetea instantáneamente (0°) al cambiar de jugador
    // (key por currentPlayerIndex), de modo que el reverso con el contenido
    // del siguiente jugador NUNCA llega a componerse en la transición.
    val rotation = remember(currentPlayerIndex) { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // M-1 (decisión opción A): sensor de movimiento activo solo con la carta
    // revelada. Al detectar movimiento (sacudida o giro de paso entre jugadores),
    // la carta vuelve al anverso SIN animación (patrón BUG-1) y se conserva el
    // mismo jugador.
    MotionDetector(
        enabled = revealed,
        onMotion = {
            cardHiddenByShake = true
            revealed = false
            scope.launch { rotation.snapTo(0f) }
        },
    )

    val isImpostor = currentPlayer.role == Role.IMPOSTOR
    val roleHeader = stringResource(if (isImpostor) R.string.role_impostor else R.string.role_your_word)
    val cardCd = stringResource(if (revealed) R.string.role_card_revealed_cd else R.string.role_card_locked_cd)
    val cardBg = if (isImpostor) gameColors.impostorBg else gameColors.civilBg
    val onCardBg = if (isImpostor) gameColors.onImpostorBg else gameColors.onCivilBg

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Barra superior: Salir | progreso | ayuda + tema [D1]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { showExitDialog = true }, modifier = Modifier.height(48.dp)) {
                Text(
                    text = stringResource(R.string.action_leave),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            Text(
                text = stringResource(R.string.role_progress, currentPlayerIndex + 1, players.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showHelpDialog = true }, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_help),
                        contentDescription = stringResource(R.string.help_title),
                    )
                }
                ThemeToggleButton()
            }
        }

        // Puntos de progreso
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            players.forEachIndexed { index, _ ->
                val dotColor = when {
                    index < currentPlayerIndex -> gameColors.success
                    index == currentPlayerIndex -> MaterialTheme.colorScheme.onBackground
                    else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                }
                Box(
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
        }
        Spacer(Modifier.height(14.dp))

        // Píldora destacada con el nombre del jugador activo (M-2)
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.primaryContainer,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = currentPlayer.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.role_your_role),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))

        // --- Naipe giratorio (anverso: ojo; reverso: rol/palabra/pista) ---
        FlipCard(
            rotation = rotation,
            modifier = Modifier
                .fillMaxWidth(0.62f)
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, gameColors.cardFrontBorder, RoundedCornerShape(20.dp))
                .semantics {
                    contentDescription = cardCd
                }
                .clickable {
                    if (!revealed) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    revealed = !revealed
                    scope.launch { rotation.animateTo(if (revealed) 180f else 0f, tween(500)) }
                },
            front = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gameColors.cardFrontBg)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        // BUG-3: el ojo ocupa el máximo posible de la carta.
                        Icon(
                            painter = painterResource(R.drawable.ic_eye),
                            contentDescription = null,
                            tint = gameColors.eye,
                            modifier = Modifier
                                .fillMaxWidth(0.96f)
                                .aspectRatio(1f),
                        )
                        // Pupila centrada sobre el ojo, con color de contraste por tema (BUG-4).
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.30f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(gameColors.eyePupil),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.role_tap_to_view),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = gameColors.warn,
                    )
                    Spacer(Modifier.height(4.dp))
                }
            },
            back = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(cardBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = roleHeader,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = onCardBg,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(10.dp))
                        if (isImpostor) {
                            if (config.showHintToImpostor) {
                                Text(
                                    text = stringResource(R.string.role_your_hint),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = onCardBg.copy(alpha = 0.85f),
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = currentWord.hint,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = onCardBg,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        } else {
                            Text(
                                text = currentWord.word,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = onCardBg,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            },
        )

        Spacer(Modifier.height(18.dp))

        // Estado
        if (revealed) {
            Text(
                text = "✔ ${stringResource(R.string.role_seen_title)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = gameColors.success,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.role_seen_sub),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else if (cardHiddenByShake) {
            Text(
                text = stringResource(R.string.role_card_hidden),
                style = MaterialTheme.typography.bodyMedium,
                color = gameColors.warn,
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                text = stringResource(R.string.role_must_view),
                style = MaterialTheme.typography.bodyMedium,
                color = gameColors.warn,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                if (isLastPlayer) onAllPlayersSawRoles() else onPlayerSawRole(currentPlayerIndex)
            },
            enabled = revealed || cardHiddenByShake,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
        ) {
            Text(
                text = stringResource(if (isLastPlayer) R.string.role_start_game else R.string.role_next),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        if (revealed) {
            Spacer(Modifier.height(8.dp))
            // M-2: el pie indica a quién pasar el móvil (el siguiente jugador);
            // el último no tiene a quién pasarlo (el botón pasa a ser Empezar Partida).
            val nextPlayerIndex = currentPlayerIndex + 1
            if (nextPlayerIndex < players.size) {
                Text(
                    text = stringResource(R.string.role_pass_phone, players[nextPlayerIndex].name),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            Spacer(Modifier.height(24.dp))
        }
    }

    // Confirmación al salir de la partida
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.role_exit_title)) },
            text = { Text(stringResource(R.string.role_exit_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
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
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    // Cómo se juega (punto 9, alcance mínimo: pantalla sencilla de instrucciones)
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text(stringResource(R.string.help_title)) },
            text = {
                Column {
                    listOf(
                        R.string.help_line1,
                        R.string.help_line2,
                        R.string.help_line3,
                        R.string.help_line4,
                        R.string.help_line5,
                    ).forEach { line ->
                        Text(
                            text = "• " + stringResource(line),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

/** Naipe que gira sobre su eje vertical; cambia anverso/reverso en los 90°. */
@Composable
private fun FlipCard(
    rotation: Animatable<Float, AnimationVector1D>,
    modifier: Modifier = Modifier,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit,
) {
    val r = rotation.value
    Box(
        modifier = modifier.graphicsLayer {
            rotationY = r
            cameraDistance = 8f * density
        },
    ) {
        if (r <= 90f) {
            Box(Modifier.fillMaxSize()) { front() }
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) { back() }
        }
    }
}
