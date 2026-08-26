package com.impostor.game.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.impostor.game.game.GameConfig
import com.impostor.game.game.getAllCategories

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

    val categories = getAllCategories()
    val maxImpostors = (playerNames.size - 1).coerceAtLeast(1)
    val shownImpostors = impostorCount.coerceAtMost(maxImpostors)
    val canStartGame = playerNames.size >= 3 && shownImpostors < playerNames.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "El Impostor",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Configura la partida",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(20.dp))

        // --- Jugadores ---
        Text(
            text = "Jugadores (${playerNames.size})",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newName,
                onValueChange = { if (it.length <= 20) newName = it },
                placeholder = { Text("Nombre del jugador") },
                singleLine = true,
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
            ) {
                Text("Añadir")
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
                IconButton(onClick = { playerNames = playerNames.filterIndexed { i, _ -> i != index }.toMutableList() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar a $name",
                        tint = Color(0xFFF87171),
                    )
                }
            }
        }

        if (playerNames.size < 3) {
            Text(
                text = "Necesitas al menos 3 jugadores",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFBBF24),
            )
        }
        Spacer(Modifier.height(20.dp))

        // --- Número de impostores ---
        Text(
            text = "Número de impostores",
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
            ) {
                Text("−", style = MaterialTheme.typography.headlineSmall)
            }
            Text(
                text = "$shownImpostors",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(56.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            FilledTonalButton(
                onClick = { impostorCount = (impostorCount + 1).coerceAtMost(maxImpostors) },
                enabled = shownImpostors < maxImpostors,
            ) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        }
        Text(
            text = "Máximo: $maxImpostors (siempre menos que jugadores)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))

        // --- Pista para el impostor ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Pista para el impostor",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = if (showHintToImpostor) "El impostor verá una pista" else "Sin pista, más difícil",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(checked = showHintToImpostor, onCheckedChange = { showHintToImpostor = it })
        }
        Spacer(Modifier.height(16.dp))

        // --- Categoría ---
        Text(
            text = "Categoría de palabras",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        // ExposedDropdownMenuBox: patrón oficial de M3. El toque en el campo abre el menú
        // (el patrón anterior con Modifier.clickable no funcionaba: el TextField consume el gesto).
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
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            categoryMenuOpen = false
                        },
                    )
                }
            }
        }
        Text(
            text = if (selectedCategory == "Todas") {
                "Palabras de todas las categorías"
            } else {
                "Solo palabras de $selectedCategory"
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
                        category = selectedCategory.takeIf { it != "Todas" },
                    ),
                )
            },
            enabled = canStartGame,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(
                text = "Empezar Partida",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        if (canStartGame) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${playerNames.size} jugadores, $shownImpostors impostor${if (shownImpostors > 1) "es" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
