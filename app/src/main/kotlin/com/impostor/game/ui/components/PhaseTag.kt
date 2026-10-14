package com.impostor.game.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Etiqueta de fase del juego (género: *juego de deducción social*).
 *
 * El género se define por la separación entre el **conocimiento privado**
 * (cada jugador mira su carta en secreto) y la **fase pública** en la que se
 * acusa, se discute y se vota (Wikipedia, «Social deduction game» /
 * «Juego de deducción social»). Esta etiqueta nombra esa fase en la pantalla
 * para que el paso del móvil y el debate no se confundan.
 *
 * Solo aporta jerarquía visual: el texto va en `onSurfaceVariant` sobre
 * `surfaceVariant` (contraste ≈9:1 en oscuro y ≈8,3:1 en claro).
 */
@Composable
fun PhaseTag(text: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        )
    }
}
