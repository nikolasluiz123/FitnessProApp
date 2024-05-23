package br.com.fitnesspro.core.callback

import br.com.fitnesspro.core.enums.EnumDialogType


fun interface IShowDialogCallback {
    fun onShow(type: EnumDialogType, message: String, onConfirm: () -> Unit, onCancel: () -> Unit)
}