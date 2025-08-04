package br.com.fitnesspro.scheduler.ui.state

import br.com.fitnesspro.compose.components.fields.state.DatePickerTextField
import br.com.fitnesspro.compose.components.fields.state.DayWeeksSelectorField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.fields.state.TimePickerTextField
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.IThrowableUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.usecase.scheduler.CompromiseRecurrentConfig
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.tuple.PersonTuple

data class CompromiseUIState(
    val title: String = "",
    val subtitle: String? = null,
    val professional: PagedDialogListTextField<PersonTuple> = PagedDialogListTextField(),
    val member: PagedDialogListTextField<PersonTuple> = PagedDialogListTextField(),
    val dateStart: DatePickerTextField = DatePickerTextField(),
    val dateEnd: DatePickerTextField = DatePickerTextField(),
    val hourStart: TimePickerTextField = TimePickerTextField(),
    val hourEnd: TimePickerTextField = TimePickerTextField(),
    val observation: TextField = TextField(),
    val dayWeeksSelectorField: DayWeeksSelectorField = DayWeeksSelectorField(),
    val recurrent: Boolean = false,
    val userType: EnumUserType? = null,
    val toScheduler: TOScheduler = TOScheduler(),
    val recurrentConfig: CompromiseRecurrentConfig = CompromiseRecurrentConfig(),
    val isEnabledDeleteButton: Boolean = false,
    val isEnabledMessageButton: Boolean = false,
    val isEnabledConfirmButton: Boolean = false,
    val hasNewMessages: Boolean = false,
    val authenticatedPerson: TOPerson = TOPerson(),
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { }
): ILoadingUIState, IThrowableUIState