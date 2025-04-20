package br.com.fitnesspro.common.ui.state

import br.com.fitnesspro.common.ui.bottomsheet.registeruser.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.common.ui.screen.registeruser.decorator.AcademyGroupDecorator
import br.com.fitnesspro.compose.components.fields.state.DatePickerTextField
import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.TabState
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser

data class RegisterUserUIState(
    val title: String? = null,
    val subtitle: String? = null,
    val context: EnumOptionsBottomSheetRegisterUser? = null,
    val toPerson: TOPerson = TOPerson(user = TOUser()),
    val tabState: TabState = TabState(),
    val name: TextField = TextField(),
    val email: TextField = TextField(),
    val password: TextField = TextField(),
    val birthDate: DatePickerTextField = DatePickerTextField(),
    val phone: TextField = TextField(),
    val userType: DropDownTextField<EnumUserType> = DropDownTextField(),
    val isVisibleFieldPhone: Boolean = false,
    val isRegisterServiceAuth: Boolean = false,
    val academies: List<AcademyGroupDecorator> = mutableListOf(),
    val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { },
): ILoadingUIState