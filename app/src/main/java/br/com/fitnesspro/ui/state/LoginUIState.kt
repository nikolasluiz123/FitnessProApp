package br.com.fitnesspro.ui.state

import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.callback.IShowDialogCallback
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.state.IDialogUIState
import br.com.fitnesspro.core.state.ILoadingUIState

data class LoginUIState(
    val email: TextField = TextField(),
    val password: TextField = TextField(),
    override val dialogMessage: String = "",
    override val showDialog: Boolean = false,
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { },
    override val dialogType: EnumDialogType = EnumDialogType.ERROR,
    override val onHideDialog: () -> Unit = { },
    override val onShowDialog: IShowDialogCallback? = null,
    override val onConfirm: () -> Unit = { },
    override val onCancel: () -> Unit = { }
) : ILoadingUIState, IDialogUIState