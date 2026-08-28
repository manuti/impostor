package com.impostor.game.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.impostor.game.R
import com.impostor.game.ui.theme.LocalDarkTheme
import com.impostor.game.ui.theme.LocalOnToggleTheme

/** Botón sol/luna para alternar tema oscuro/claro (requisito D1). */
@Composable
fun ThemeToggleButton(modifier: Modifier = Modifier) {
    val darkTheme = LocalDarkTheme.current
    val onToggle = LocalOnToggleTheme.current
    IconButton(
        onClick = onToggle,
        modifier = modifier.size(48.dp),
    ) {
        Icon(
            painter = painterResource(if (darkTheme) R.drawable.ic_sun else R.drawable.ic_moon),
            contentDescription = stringResource(
                if (darkTheme) R.string.theme_to_light_cd else R.string.theme_to_dark_cd
            ),
        )
    }
}
