package br.com.fitnesspro.compose.components.fields.state

import br.com.fitnesspro.core.menu.ITupleListItem

data class DialogListTextField<T: ITupleListItem>(
    val dialogListState: DialogListState<T> = DialogListState(),
    override val value: String = "",
    override val onChange: (String) -> Unit = { },
    override val errorMessage: String = ""
): ITextField