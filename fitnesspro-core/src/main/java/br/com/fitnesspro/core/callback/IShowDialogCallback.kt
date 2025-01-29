package br.com.fitnesspro.core.callback

import br.com.fitnesspro.core.enums.EnumDialogType

/**
 * Interface para representar o callback de uma dialog.
 *
 * O uso dessa interface normalmente deverá ser utilizado em conjunto com a implementação de [br.com.fitnesspro.core.state.IDialogUIState]
 * no UIState da tela em questão.
 */
fun interface IShowDialogCallback {

    /**
     * Função que deve ser usada sempre se quiser exibir uma dialog.
     */
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