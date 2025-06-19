package br.com.fitnesspro.compose.components.fields.state

import br.com.fitnesspro.core.menu.ITupleListItem

data class DialogListState<T: ITupleListItem>(
    val dialogTitle: String = "",
    val dataList: List<T> = emptyList(),
    val show: Boolean = false,
    val onShow: () -> Unit = { },
    val onHide: () -> Unit = { },
    val onDataListItemClick: (T) -> Unit = { },
    val onSimpleFilterChange: (String) -> Unit = { }
)