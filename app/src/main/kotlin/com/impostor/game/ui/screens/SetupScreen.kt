package com.impostor.game.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.impostor.game.R
import com.impostor.game.game.GameConfig
import com.impostor.game.game.getAllCategories
import com.impostor.game.ui.theme.gameColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    initialPlayerNames: List<String>,
    initialCategory: String,
    onStartGame: (List<String>, GameConfig) -> Unit,
) {
    var playerNames by remember { mutableStateOf(initialPlayerNames.toMutableList()) }
    var newName by remember { mutableStateOf("") }
    var impostorCount by remember { mutableStateOf(1) }
    var showHintToImpostor by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var categoryMenuOpen by remember { mutableStateOf(false) }
    var pendingRemove by remember { mutableStateOf<String?>(null) }

    val allCategoriesLabel = stringResource(R.string.setup_category_all)
    val categories = listOf(allCategoriesLabel) + getAllCategories()
    val maxImpostors = (playerNames.size - 1).coerceAtLeast(1)
    val shownImpostors = impostorCount.coerceAtMost(maxImpostors)
    val canStartGame = playerNames.size >= 3 && shownImpostors < playerNames.size
    val gameColors = MaterialTheme.gameColors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.setup_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.setup_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(20.dp))

        // --- Jugadores ---
        Text(
            text = stringResource(R.string.setup_players_section, playerNames.size),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newName,
                onValueChange = { if (it.length <= 20) newName = it },
                placeholder = { Text(stringResource(R.string.setup_player_name_placeholder)) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    val trimmed = newName.trim()
                    if (trimmed.isNotEmpty() && trimmed !in playerNames) {
                        playerNames = (playerNames + trimmed).toMutableList()
                        newName = ""
                    }
                },
                enabled = newName.isNotBlank(),
                modifier = Modifier.height(56.dp),
            ) {
                Text(
                    text = stringResource(R.string.setup_add_player),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        playerNames.forEachIndexed { index, name ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = { pendingRemove = name },
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.setup_remove_player_cd, name),
                        tint = gameColors.impostor,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }

        if (playerNames.size < 3) {
            Text(
                text = stringResource(R.string.setup_min_players),
                style = MaterialTheme.typography.bodySmall,
                color = gameColors.warn,
            )
        }
        Spacer(Modifier.height(20.dp))

        // --- Número de impostores ---
        Text(
            text = stringResource(R.string.setup_impostors_section),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            FilledTonalButton(
                onClick = { impostorCount = (impostorCount - 1).coerceAtLeast(1) },
                enabled = impostorCount > 1,
                colors = ButtonDefaults.filledTonalButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                ),
                modifier = Modifier.size(width = 64.dp, height = 56.dp),
            ) {
                Text("−", style = MaterialTheme.typography.headlineMedium)
            }
            Text(
                text = "$shownImpostors",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.width(64.dp),
                textAlign = TextAlign.Center,
            )
            FilledTonalButton(
                onClick = { impostorCount = (impostorCount + 1).coerceAtMost(maxImpostors) },
                enabled = shownImpostors < maxImpostors,
                colors = ButtonDefaults.filledTonalButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                ),
                modifier = Modifier.size(width = 64.dp, height = 56.dp),
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
        Text(
            text = stringResource(R.string.setup_impostors_max, maxImpostors),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))

        // --- Pista para el impostor ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.setup_hint_switch),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(if (showHintToImpostor) R.string.setup_hint_on else R.string.setup_hint_off),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(checked = showHintToImpostor, onCheckedChange = { showHintToImpostor = it })
        }
        Spacer(Modifier.height(16.dp))

        // --- Categoría ---
        Text(
            text = stringResource(R.string.setup_category_section),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        // ExposedDropdownMenuBox: patrón oficial de M3 (accesible con TalkBack).
        ExposedDropdownMenuBox(
            expanded = categoryMenuOpen,
            onExpandedChange = { categoryMenuOpen = it },
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuOpen) },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(
                expanded = categoryMenuOpen,
                onDismissRequest = { categoryMenuOpen = false },
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            selectedCategory = category
                            categoryMenuOpen = false
                        },
                    )
                }
            }
        }
        Text(
            text = if (selectedCategory == allCategoriesLabel) {
                stringResource(R.string.setup_category_all_hint)
            } else {
                stringResource(R.string.setup_category_only_hint, selectedCategory)
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(28.dp))

        // --- Empezar ---
        Button(
            onClick = {
                onStartGame(
                    playerNames,
                    GameConfig(
                        impostorCount = shownImpostors,
                        showHintToImpostor = showHintToImpostor,
                        category = selectedCategory.takeIf { it != allCategoriesLabel },
                    ),
                )
            },
            enabled = canStartGame,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
        ) {
            Text(
                text = stringResource(R.string.setup_start),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        if (canStartGame) {
            Spacer(Modifier.height(8.dp))
            val playersSummary = pluralStringResource(R.plurals.setup_summary_players, playerNames.size, playerNames.size)
            val impostorsSummary = pluralStringResource(R.plurals.setup_summary_impostors, shownImpostors, shownImpostors)
            Text(
                text = "$playersSummary, $impostorsSummary",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    // Confirmación antes de eliminar un jugador (requisito de la fase 2).
    pendingRemove?.let { name ->
        AlertDialog(
            onDismissRequest = { pendingRemove = null },
            title = { Text(stringResource(R.string.setup_confirm_remove_title)) },
            text = { Text(stringResource(R.string.setup_confirm_remove_text, name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        playerNames = playerNames.filterNot { it == name }.toMutableList()
                        pendingRemove = null
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
                TextButton(onClick = { pendingRemove = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}
