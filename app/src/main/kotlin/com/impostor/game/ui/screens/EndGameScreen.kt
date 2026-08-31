package com.impostor.game.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impostor.game.R
import com.impostor.game.game.Player
import com.impostor.game.game.Role
import com.impostor.game.game.WordPair
import com.impostor.game.ui.components.ThemeToggleButton
import com.impostor.game.ui.theme.gameColors

@Composable
fun EndGameScreen(
    winner: Role,
    players: List<Player>,
    currentWord: WordPair,
    onPlayAgain: () -> Unit,
) {
    val civilsWon = winner == Role.CIVILIAN
    val impostors = players.filter { it.role == Role.IMPOSTOR }
    val civilians = players.filter { it.role == Role.CIVILIAN }
    val gameColors = MaterialTheme.gameColors

    val accent = if (civilsWon) gameColors.civil else gameColors.impostor
    val accentBg = if (civilsWon) gameColors.civilBg else gameColors.impostorBg
    val onAccentBg = if (civilsWon) gameColors.onCivilBg else gameColors.onImpostorBg

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Barra superior: tema claro/oscuro (M-3)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ThemeToggleButton()
        }
        Spacer(Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = accentBg.copy(alpha = 0.45f),
            border = BorderStroke(2.dp, accent.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (civilsWon) "🛡️" else "🎭",
                    fontSize = 44.sp,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(if (civilsWon) R.string.end_victory_civil else R.string.end_victory_impostor),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(if (civilsWon) R.string.end_civil_text else R.string.end_impostor_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))

                // Palabra secreta
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.25f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.end_word_was),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = currentWord.word,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(R.string.end_hint, currentWord.hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Listas de roles
                Text(
                    text = stringResource(R.string.end_impostors_list, impostors.size),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = gameColors.impostor,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(4.dp))
                PlayerChips(impostors, gameColors.impostor)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.end_civilians_list, civilians.size),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = gameColors.civil,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(4.dp))
                PlayerChips(civilians, gameColors.civil)
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
        ) {
            Text(
                text = stringResource(R.string.end_play_again),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun PlayerChips(players: List<Player>, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        players.forEach { player ->
            Surface(
                shape = RoundedCornerShape(50),
                color = color.copy(alpha = if (player.isAlive) 0.25f else 0.10f),
                border = BorderStroke(
                    1.dp,
                    color.copy(alpha = if (player.isAlive) 0.6f else 0.3f),
                ),
            ) {
                Text(
                    text = player.name,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = color.copy(alpha = if (player.isAlive) 1f else 0.5f),
                    textDecoration = if (player.isAlive) null else TextDecoration.LineThrough,
                )
            }
        }
    }
}
