package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.core.theme.FitnessProTheme

/**
 * Menu de mais opções.
 *
 * @param menuItems
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun MenuIconButton(
    menuItems: @Composable () -> Unit = { }
) {
    var showMenu by remember { mutableStateOf(false) }

    IconButtonMoreVert { showMenu = !showMenu }

    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
        menuItems()
    }
}

@Preview
@Composable
private fun MenuIconButtonPreview() {
    FitnessProTheme {
        Surface {
            MenuIconButton()
        }
    }
}