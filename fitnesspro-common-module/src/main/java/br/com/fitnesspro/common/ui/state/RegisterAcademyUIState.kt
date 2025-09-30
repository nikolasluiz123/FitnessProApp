package br.com.fitnesspro.common.ui.state

import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.fields.dropdown.state.DropDownTextField
import br.com.android.ui.compose.components.fields.text.dialog.paged.state.PagedDialogListTextField
import br.com.android.ui.compose.components.fields.text.time.state.TimePickerTextField
import br.com.android.ui.compose.components.states.ILoadingUIState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.tuple.AcademyTuple
import java.time.DayOfWeek

data class RegisterAcademyUIState(
    var title: String = "",
    var subtitle: String? = null,
    val academy: PagedDialogListTextField<AcademyTuple> = PagedDialogListTextField(),
    val dayWeek: DropDownTextField<DayOfWeek?> = DropDownTextField(),
    val start: TimePickerTextField = TimePickerTextField(),
    val end: TimePickerTextField = TimePickerTextField(),
    var toPersonAcademyTime: TOPersonAcademyTime = TOPersonAcademyTime(),
    var isEnabledInactivationButton: Boolean = false,
    override var executeLoad: Boolean = true,
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { }
): ILoadingUIState, IThrowableUIState, ISuspendedLoadUIState