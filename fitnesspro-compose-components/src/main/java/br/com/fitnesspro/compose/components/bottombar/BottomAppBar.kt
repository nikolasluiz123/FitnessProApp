package br.com.fitnesspro.compose.components.bottombar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.core.theme.GREY_400

/**
 * Componente bottom app bar padrão.
 *
 * @param modifier
 * @param actions
 * @param floatingActionButton
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun FitnessProBottomAppBar(
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = { },
    floatingActionButton: @Composable () -> Unit = { }
) {
    BottomAppBar(
        modifier = modifier,
        actions = actions,
        floatingActionButton = floatingActionButton,
        containerColor = GREY_400,
        windowInsets = WindowInsets(0.dp)
    )
}