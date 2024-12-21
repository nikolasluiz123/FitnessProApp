package br.com.fitnesspro.ui.state

import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.core.callback.IShowDialogCallback
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.state.IDialogUIState
import br.com.fitnesspro.ui.bottomsheet.registeruser.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.ui.screen.registeruser.to.TOPerson

data class RegisterUserUIState(
    val title: String? = null,
    val subtitle: String? = null,
    val context: EnumOptionsBottomSheetRegisterUser? = null,
    val toPerson: TOPerson? = null,
    val tabs: MutableList<Tab> = mutableListOf(),
    val name: Field = Field(),
    val email: Field = Field(),
    val password: Field = Field(),
    val birthDate: Field = Field(),
    val phone: Field = Field(),
    override val dialogMessage: String = "",
    override val showDialog: Boolean = false,
    override val dialogType: EnumDialogType = EnumDialogType.ERROR,
    override val onHideDialog: () -> Unit = { },
    override val onShowDialog: IShowDialogCallback? = null,
    override val onConfirm: () -> Unit = { },
    override val onCancel: () -> Unit = { },
): IDialogUIState