package br.com.fitnesspro.compose.components.bottombar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonAdd
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.core.theme.FitnessProTheme

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
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        windowInsets = WindowInsets(0.dp)
    )
}

@Preview(device = "id:small_phone")
@Composable
private fun FitnessProBottomAppBarPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProBottomAppBar(
                floatingActionButton = {
                    FloatingActionButtonAdd(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        iconColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                actions = {
                    IconButtonDelete()
                }
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun FitnessProBottomAppBarPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProBottomAppBar(
                floatingActionButton = {
                    FloatingActionButtonAdd(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        iconColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                actions = {
                    IconButtonDelete()
                }
            )
        }
    }
}