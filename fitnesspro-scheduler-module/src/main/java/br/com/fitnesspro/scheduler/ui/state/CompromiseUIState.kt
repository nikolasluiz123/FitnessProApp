package br.com.fitnesspro.scheduler.ui.state

import br.com.fitnesspro.compose.components.fields.state.DatePickerTextField
import br.com.fitnesspro.compose.components.fields.state.DayWeeksSelectorField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.fields.state.TimePickerTextField
import br.com.fitnesspro.core.callback.IShowDialogCallback
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.state.IDialogUIState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.tuple.PersonTuple
import br.com.fitnesspro.scheduler.usecase.scheduler.CompromiseRecurrentConfig

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
    override val dialogType: EnumDialogType = EnumDialogType.ERROR,
    override val dialogMessage: String = "",
    override val showDialog: Boolean = false,
    override val onShowDialog: IShowDialogCallback? = null,
    override val onHideDialog: () -> Unit = { },
    override val onConfirm: () -> Unit = { },
    override val onCancel: () -> Unit = { }
): IDialogUIState