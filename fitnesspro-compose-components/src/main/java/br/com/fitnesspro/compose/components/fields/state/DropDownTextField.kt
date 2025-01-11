package br.com.fitnesspro.compose.components.fields.state

import br.com.fitnesspro.compose.components.fields.menu.MenuItem

data class DropDownTextField<T>(
    val dataList: List<MenuItem<T>> = emptyList(),
    val dataListFiltered: List<MenuItem<T>> = emptyList(),
    val expanded: Boolean = false,
    val onDropDownExpandedChange: (Boolean) -> Unit = { },
    val onDropDownDismissRequest: () -> Unit = { },
    val onDataListItemClick: (MenuItem<T>) -> Unit = { },
    override val value: String = "",
    override val onChange: (String) -> Unit = { },
    override val errorMessage: String = ""
): ITextField