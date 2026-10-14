package com.impostor.game.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.impostor.game.R
import com.impostor.game.game.GameConfig
import com.impostor.game.game.WordRepository
import com.impostor.game.ui.components.LanguageButton
import com.impostor.game.ui.components.ThemeToggleButton
import com.impostor.game.ui.theme.gameColors
import kotlinx.coroutines.launch

/**
 * Configuración de la partida.
 *
 * Ticket estético (v0.4.1): las secciones se agrupan en tarjetas
 * `surfaceVariant` (punto 1), el botón deshabilitado usa contorno `primary` con
 * texto legible y explica *por qué* no se puede empezar (punto 2), la jerarquía
 * de títulos se refuerza con escala y espaciado en lugar de peso `Bold`
 * (punto 13), el `Switch` tiene área táctil de fila completa ≥44 dp (punto 14)
 * y el formulario de alta de jugador se desplaza a la vista al enfocar el campo
 * para que "Añadir" no quede tapado por el teclado (punto 15).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SetupScreen(
    initialPlayerNames: List<String>,
    initialCategoryId: String,
    onStartGame: (List<String>, GameConfig) -> Unit,
) {
    var playerNames by remember { mutableStateOf(initialPlayerNames.toMutableList()) }
    var newName by remember { mutableStateOf("") }
    var impostorCount by remember { mutableStateOf(1) }
    var showHintToImpostor by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf(initialCategoryId) }
    var categoryMenuOpen by remember { mutableStateOf(false) }
    var pendingRemove by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val allCategoriesLabel = stringResource(R.string.setup_category_all)
    // (id estable, nombre localizado): el id es lo que viaja a GameConfig; el nombre solo se muestra.
    val categories = listOf(WordRepository.ALL_ID to allCategoriesLabel) +
        WordRepository.choices(context)
    val selectedCategoryLabel = categories
        .firstOrNull { it.first == selectedCategoryId }
        ?.second
        ?: allCategoriesLabel
    val maxImpostors = (playerNames.size - 1).coerceAtLeast(1)
    val shownImpostors = impostorCount.coerceAtMost(maxImpostors)
    val canStartGame = playerNames.size >= 3 && shownImpostors < playerNames.size
    val gameColors = MaterialTheme.gameColors

    // Punto 15: al enfocar el campo de nombre, se trae a la vista la fila
    // completa (campo + botón "Añadir"). El inset de teclado ya lo aplica
    // `safeDrawingPadding()` en App.kt, así que aquí solo hay que garantizar
    // que esa fila quede dentro de la ventana visible al abrirse el teclado.
    val addRowRequester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    val cancelRemoveFocus = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Barra superior: título + tema claro/oscuro (M-3)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.setup_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                LanguageButton(hasPlayers = playerNames.isNotEmpty())
                ThemeToggleButton()
            }
        }
        Spacer(Modifier.height(20.dp))

        // --- Tarjeta: configuración de la partida (punto 1) ---
        SetupCard(title = stringResource(R.string.setup_config_section)) {
            // --- Categoría ---
            Text(
                text = stringResource(R.string.setup_category_section),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            // ExposedDropdownMenuBox: patrón oficial de M3 (accesible con TalkBack).
            ExposedDropdownMenuBox(
                expanded = categoryMenuOpen,
                onExpandedChange = { categoryMenuOpen = it },
            ) {
                OutlinedTextField(
                    value = selectedCategoryLabel,
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
                    categories.forEach { (id, label) ->
                        DropdownMenuItem(
                            text = { Text(label, style = MaterialTheme.typography.bodyLarge) },
                            onClick = {
                                selectedCategoryId = id
                                categoryMenuOpen = false
                            },
                        )
                    }
                }
            }
            Text(
                text = if (selectedCategoryId == WordRepository.ALL_ID) {
                    stringResource(R.string.setup_category_all_hint)
                } else {
                    stringResource(R.string.setup_category_only_hint, selectedCategoryLabel)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(20.dp))

            // --- Número de impostores ---
            Text(
                text = stringResource(R.string.setup_impostors_section),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
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
                    color = MaterialTheme.colorScheme.onSurface,
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
            Spacer(Modifier.height(20.dp))

            // --- Pista para el impostor ---
            // Punto 14: toda la fila es el control (área táctil ≥44 dp, muy por
            // encima del thumb del Switch); el Switch queda como indicador.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .toggleable(
                        value = showHintToImpostor,
                        role = Role.Switch,
                        onValueChange = { showHintToImpostor = it },
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.setup_hint_switch),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(if (showHintToImpostor) R.string.setup_hint_on else R.string.setup_hint_off),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = showHintToImpostor, onCheckedChange = null)
            }
        }

        Spacer(Modifier.height(24.dp))

        // --- Empezar ---
        if (canStartGame) {
            Button(
                onClick = {
                    onStartGame(
                        playerNames,
                        GameConfig(
                            impostorCount = shownImpostors,
                            showHintToImpostor = showHintToImpostor,
                            category = selectedCategoryId.takeIf { it != WordRepository.ALL_ID },
                        ),
                    )
                },
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
            Spacer(Modifier.height(10.dp))
            val playersSummary = pluralStringResource(R.plurals.setup_summary_players, playerNames.size, playerNames.size)
            val impostorsSummary = pluralStringResource(R.plurals.setup_summary_impostors, shownImpostors, shownImpostors)
            Text(
                text = stringResource(R.string.setup_summary_joined, playersSummary, impostorsSummary),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            // Punto 2: deshabilitado pero legible (contorno primary tenue +
            // texto onSurfaceVariant, no gris sobre gris) y con el motivo.
            OutlinedButton(
                onClick = {},
                enabled = false,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
            ) {
                Text(
                    text = stringResource(R.string.setup_start),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.setup_min_players),
                style = MaterialTheme.typography.bodySmall,
                color = gameColors.warn,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(24.dp))

        // --- Tarjeta: añadir jugadores ---
        SetupCard(title = stringResource(R.string.setup_add_players_section)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(addRowRequester),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { if (it.length <= 20) newName = it },
                    placeholder = { Text(stringResource(R.string.setup_player_name_placeholder)) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { state ->
                            if (state.isFocused) {
                                scope.launch { addRowRequester.bringIntoView() }
                            }
                        },
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
            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.setup_players_section, playerNames.size),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(6.dp))

            playerNames.forEach { name ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
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
        }
        Spacer(Modifier.height(20.dp))
    }

    // Confirmación antes de eliminar un jugador (requisito de la fase 2).
    // Punto 11: el foco inicial va a "Cancelar" (la opción no destructiva).
    pendingRemove?.let { name ->
        LaunchedEffect(name) { cancelRemoveFocus.requestFocus() }
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
                TextButton(
                    onClick = { pendingRemove = null },
                    modifier = Modifier.focusRequester(cancelRemoveFocus),
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

/**
 * Tarjeta de sección: agrupa controles relacionados sobre `surfaceVariant`
 * (punto 1) con el mismo lenguaje visual que la tarjeta de `EndGameScreen`
 * (punto 12). El título usa `titleMedium` en semibold, sin `Bold` (punto 13).
 */
@Composable
private fun SetupCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}
