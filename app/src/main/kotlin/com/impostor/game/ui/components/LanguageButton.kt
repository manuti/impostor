package com.impostor.game.ui.components

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.impostor.game.R

private const val LANGUAGE_ES = "es-ES"
private const val LANGUAGE_EN = "en-GB"

/**
 * Selector de idioma dentro de la app (D-F3-2, opción B).
 *
 * Usa AppCompatDelegate.setApplicationLocales: en Android 13+ delega en el ajuste
 * de idioma por app del sistema, y en versiones anteriores lo aplica AppCompat y
 * recuerda la elección entre arranques.
 *
 * El cambio de idioma recrea la actividad, así que si hay jugadores añadidos se
 * pide confirmación antes de aplicarlo para no perder los nombres por error.
 */
@Composable
fun LanguageButton(hasPlayers: Boolean, modifier: Modifier = Modifier) {
    var dialogOpen by remember { mutableStateOf(false) }
    var pendingTag by remember { mutableStateOf<String?>(null) }
    val systemLanguage = LocalConfiguration.current.locales[0].language
    val appLocales = AppCompatDelegate.getApplicationLocales()
    val appLanguage = if (appLocales.isEmpty) null else appLocales.get(0)?.language
    val effectiveLanguage = appLanguage ?: systemLanguage
    val chooseLabel = stringResource(R.string.language_cd)
    val applyLanguage = { tag: String ->
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }

    TextButton(
        onClick = { dialogOpen = true },
        modifier = modifier.semantics { contentDescription = chooseLabel },
    ) {
        Text(
            text = if (effectiveLanguage == "es") "ES" else "EN",
            style = MaterialTheme.typography.labelLarge,
        )
    }

    if (dialogOpen) {
        LanguageDialog(
            selectedLanguage = effectiveLanguage,
            onDismiss = { dialogOpen = false },
            onSelect = { tag ->
                dialogOpen = false
                val currentTag = if (effectiveLanguage == "es") LANGUAGE_ES else LANGUAGE_EN
                if (tag != currentTag) {
                    if (hasPlayers) pendingTag = tag else applyLanguage(tag)
                }
            },
        )
    }

    pendingTag?.let { tag ->
        ConfirmLanguageChangeDialog(
            onDismiss = { pendingTag = null },
            onConfirm = {
                pendingTag = null
                applyLanguage(tag)
            },
        )
    }
}

@Composable
private fun LanguageDialog(
    selectedLanguage: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.language_title)) },
        text = {
            Column(Modifier.selectableGroup()) {
                LanguageOption(
                    label = stringResource(R.string.language_es),
                    selected = selectedLanguage == "es",
                    onSelect = { onSelect(LANGUAGE_ES) },
                )
                LanguageOption(
                    label = stringResource(R.string.language_en),
                    selected = selectedLanguage != "es",
                    onSelect = { onSelect(LANGUAGE_EN) },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}

/** Aviso previo: cambiar de idioma reinicia la pantalla y pierde los jugadores añadidos. */
@Composable
private fun ConfirmLanguageChangeDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.language_change_title)) },
        text = { Text(stringResource(R.string.language_change_text)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.language_change_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}

@Composable
private fun LanguageOption(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}
