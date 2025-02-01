package br.com.fitnesspro.compose.components.fields.state

import androidx.paging.PagingData
import br.com.fitnesspro.core.menu.ITupleListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class PagedDialogListState<T: ITupleListItem>(
    val dialogTitle: String = "",
    val dataList: Flow<PagingData<T>> = emptyFlow(),
    val show: Boolean = false,
    val onShow: () -> Unit = { },
    val onHide: () -> Unit = { },
    val onDataListItemClick: (T) -> Unit = { },
    val onSimpleFilterChange: (String) -> Unit = { }
)