package br.com.fitnesspro.ui.state

import br.com.fitnesspro.compose.components.fields.state.SwitchButtonField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.fields.state.TimePickerTextField
import br.com.fitnesspro.core.callback.IShowDialogCallback
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.state.IDialogUIState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOSchedulerConfig

data class SchedulerConfigUIState(
    val alarm: SwitchButtonField = SwitchButtonField(),
    val notification: SwitchButtonField = SwitchButtonField(),
    val minEventDensity: TextField = TextField(),
    val maxEventDensity: TextField = TextField(),
    val startWorkTime: TimePickerTextField = TimePickerTextField(),
    val endWorkTime: TimePickerTextField = TimePickerTextField(),
    val startBreakTime: TimePickerTextField = TimePickerTextField(),
    val endBreakTime: TimePickerTextField = TimePickerTextField(),
    val userType: EnumUserType? = null,
    val toConfig: TOSchedulerConfig = TOSchedulerConfig(),
    override val dialogType: EnumDialogType = EnumDialogType.ERROR,
    override val dialogMessage: String = "",
    override val showDialog: Boolean = false,
    override val onShowDialog: IShowDialogCallback? = null,
    override val onHideDialog: () -> Unit = {},
    override val onConfirm: () -> Unit = {},
    override val onCancel: () -> Unit = {}
): IDialogUIState