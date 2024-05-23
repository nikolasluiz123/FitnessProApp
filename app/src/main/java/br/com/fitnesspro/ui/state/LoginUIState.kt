package br.com.fitnesspro.ui.state

import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.core.callback.IShowDialogCallback
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.state.IDialogUIState
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.IValidationUIState

data class LoginUIState(
    val email: Field = Field(),
    val password: Field = Field(),
    override val dialogMessage: String = "",
    override val showDialog: Boolean = false,
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { },
    override val onValidate: () -> Boolean = { false },
    override val dialogType: EnumDialogType = EnumDialogType.ERROR,
    override val onHideDialog: () -> Unit = { },
    override val onShowDialog: IShowDialogCallback? = null,
    override val onConfirm: () -> Unit = { },
    override val onCancel: () -> Unit = { },
    override var internalErrorMessage: String = ""
) : IValidationUIState, ILoadingUIState, IDialogUIState