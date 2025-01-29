package br.com.fitnesspro.core.state

import br.com.fitnesspro.core.callback.IShowDialogCallback
import br.com.fitnesspro.core.enums.EnumDialogType

data class MessageDialogState(
    val dialogType: EnumDialogType = EnumDialogType.ERROR,
    val dialogMessage: String = "",
    val showDialog: Boolean = false,
    val onShowDialog: IShowDialogCallback? = null,
    val onHideDialog: () -> Unit = { },
    val onConfirm: () -> Unit = { },
    val onCancel: () -> Unit = { }
)