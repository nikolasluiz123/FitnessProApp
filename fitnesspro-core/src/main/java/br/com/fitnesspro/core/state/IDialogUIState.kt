package br.com.fitnesspro.core.state

import br.com.fitnesspro.core.callback.IShowDialogCallback
import br.com.fitnesspro.core.enums.EnumDialogType

interface IDialogUIState {
    val dialogType: EnumDialogType
    val dialogMessage: String
    val showDialog: Boolean
    val onShowDialog: IShowDialogCallback?
    val onHideDialog: () -> Unit
    val onConfirm: () -> Unit
    val onCancel: () -> Unit
    var internalErrorMessage: String
}