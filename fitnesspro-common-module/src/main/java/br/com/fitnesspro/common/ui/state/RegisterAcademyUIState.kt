package br.com.fitnesspro.common.ui.state

import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TimePickerTextField
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.IThrowableUIState
import br.com.fitnesspro.core.state.MessageDialogState
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
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { }
): ILoadingUIState, IThrowableUIState