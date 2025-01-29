package br.com.fitnesspro.core.callback

import br.com.fitnesspro.core.enums.EnumDialogType

fun interface IShowDialogCallback {
    fun onShow(type: EnumDialogType, message: String, onConfirm: () -> Unit, onCancel: () -> Unit)
}

fun IShowDialogCallback.showErrorDialog(message: String) {
    this.onShow(
        type = EnumDialogType.ERROR,
        message = message,
        onConfirm = { },
        onCancel = { }
    )
}

fun IShowDialogCallback.showConfirmationDialog(message: String, onConfirm: () -> Unit) {
    this.onShow(
        type = EnumDialogType.CONFIRMATION,
        message = message,
        onConfirm = onConfirm,
        onCancel = { }
    )
}

fun IShowDialogCallback.showInformationDialog(message: String) {
    this.onShow(
        type = EnumDialogType.INFORMATION,
        message = message,
        onConfirm = { },
        onCancel = { }
    )
}