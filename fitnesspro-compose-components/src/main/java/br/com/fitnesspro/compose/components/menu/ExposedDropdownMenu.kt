package br.com.fitnesspro.compose.components.menu

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonArrowDown
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.state.Field

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DefaultExposedDropdownMenu(
    field: Field,
    labelResId: Int,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onMenuDismissRequest: () -> Unit,
    onItemClick: (MenuItem<T>) -> Unit,
    items: List<MenuItem<T>>,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
    ) {
        OutlinedTextFieldValidation(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            field = field,
            label = stringResource(labelResId),
            trailingIcon = {
                IconButtonArrowDown(
                    modifier = Modifier.rotate(if (expanded) 180f else 0f),
                    onClick = { }
                )
            },
            readOnly = true
        )

        DropdownMenu(
            modifier = Modifier
                .exposedDropdownSize()
                .fillMaxHeight(fraction = 0.5f),
            expanded = expanded,
            onDismissRequest = onMenuDismissRequest
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item.label) },
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}