package br.com.fitnesspro.compose.components.fields.state

import br.com.fitnesspro.core.menu.ITupleListItem

data class PagedDialogListTextField<T: ITupleListItem>(
    val dialogListState: PagedDialogListState<T> = PagedDialogListState(),
    override val value: String = "",
    override val onChange: (String) -> Unit = { },
    override val errorMessage: String = ""
): ITextField