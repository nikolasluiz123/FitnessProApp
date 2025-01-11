package br.com.fitnesspro.ui.state

import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.core.callback.IShowDialogCallback
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.state.IDialogUIState
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.ui.bottomsheet.registeruser.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.ui.screen.registeruser.decorator.AcademyGroupDecorator

data class RegisterUserUIState(
    val title: String? = null,
    val subtitle: String? = null,
    val context: EnumOptionsBottomSheetRegisterUser? = null,
    val toPerson: TOPerson? = null,
    val tabs: MutableList<Tab> = mutableListOf(),
    val name: TextField = TextField(),
    val email: TextField = TextField(),
    val password: TextField = TextField(),
    val birthDate: TextField = TextField(),
    val phone: TextField = TextField(),
    val isVisibleFieldPhone: Boolean = false,
    val academies: List<AcademyGroupDecorator> = mutableListOf(),
    override val dialogMessage: String = "",
    override val showDialog: Boolean = false,
    override val dialogType: EnumDialogType = EnumDialogType.ERROR,
    override val onHideDialog: () -> Unit = { },
    override val onShowDialog: IShowDialogCallback? = null,
    override val onConfirm: () -> Unit = { },
    override val onCancel: () -> Unit = { },
): IDialogUIState