package br.com.fitnesspro.compose.components.fields.menu

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonArrowDown
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.enums.EnumDropdownMenuTestTags.DROP_DOWN_MENU_ITEM
import br.com.fitnesspro.compose.components.fields.enums.EnumDropdownMenuTestTags.DROP_DOWN_MENU_OUTLINED_TEXT_FIELD
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.ITextField

@Composable
fun <T> DefaultExposedDropdownMenu(
    field: DropDownTextField<T>,
    labelResId: Int,
    modifier: Modifier = Modifier
) {
    DefaultExposedDropdownMenu(
        field = field,
        labelResId = labelResId,
        expanded = field.expanded,
        onExpandedChange = field.onDropDownExpandedChange,
        onMenuDismissRequest = field.onDropDownDismissRequest,
        onItemClick = field.onDataListItemClick,
        items = field.dataListFiltered,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DefaultExposedDropdownMenu(
    field: ITextField,
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
                .testTag(DROP_DOWN_MENU_OUTLINED_TEXT_FIELD.name)
                .menuAnchor()
                .fillMaxWidth(),
            field = field,
            label = stringResource(labelResId),
            trailingIcon = {
                IconButtonArrowDown(
                    modifier = Modifier
                        .testTag(OUTLINED_TEXT_FIELD_TRAILING_ICON.name)
                        .rotate(if (expanded) 180f else 0f),
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
                    modifier = Modifier.testTag(DROP_DOWN_MENU_ITEM.name),
                    text = { Text(text = item.label) },
                    onClick = {
                        items.selectValue(item.value)
                        onItemClick(item)
                    }
                )
            }
        }
    }
}

fun <T> List<MenuItem<T>>.selectValue(value: T) {
    forEach { it.selected = false }
    first { it.value == value }.selected = true
}