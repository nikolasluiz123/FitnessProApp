package br.com.fitnesspro.compose.components.fields.state

import androidx.paging.PagingData
import br.com.fitnesspro.core.menu.ITupleListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class PagedDialogListTextField<T: ITupleListItem>(
    val dialogTitle: String = "",
    val dataList: Flow<PagingData<T>> = emptyFlow(),
    val show: Boolean = false,
    val onShow: () -> Unit = { },
    val onHide: () -> Unit = { },
    val onDataListItemClick: (T) -> Unit = { },
    val onSimpleFilterChange: (String) -> Unit = { },
    override val value: String = "",
    override val onChange: (String) -> Unit = { },
    override val errorMessage: String = ""
): ITextField