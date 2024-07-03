package br.com.fitnesspro.compose.components.menu

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.compose.components.list.LazyVerticalList
import br.com.fitnesspro.core.theme.FitnessProTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FitnessProDropDownMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<MenuItem<T>>,
    onItemClick: (MenuItem<T>) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        LazyVerticalList(items = items) {
            DropdownMenuItem(
                text = {
                    Text(text = it.label)
                },
                onClick = { onItemClick(it) }
            )
        }
    }
}

@Preview
@Composable
private fun FitnessProDropDownMenuPreview() {
    FitnessProTheme {
        Surface {
            FitnessProDropDownMenu(
                expanded = true,
                onExpandedChange = {},
                items = listOf(
                    MenuItem(
                        label = "Item 1",
                        value = 1
                    ),
                    MenuItem(
                        label = "Item 2",
                        value = 2
                    )
                ),
                onItemClick = { }
            )
        }
    }
}