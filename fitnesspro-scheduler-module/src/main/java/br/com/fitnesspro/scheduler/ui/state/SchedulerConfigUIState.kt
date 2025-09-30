package br.com.fitnesspro.scheduler.ui.state

import br.com.android.ui.compose.components.buttons.switchbutton.state.SwitchButtonField
import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.fields.text.state.TextField
import br.com.android.ui.compose.components.states.ILoadingUIState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOSchedulerConfig

data class SchedulerConfigUIState(
    val notification: SwitchButtonField = SwitchButtonField(),
    val notificationAntecedenceTime: TextField = TextField(),
    val minEventDensity: TextField = TextField(),
    val maxEventDensity: TextField = TextField(),
    val toPerson: TOPerson? = null,
    val toConfig: TOSchedulerConfig = TOSchedulerConfig(),
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { },
    override var executeLoad: Boolean = true
): ILoadingUIState, IThrowableUIState, ISuspendedLoadUIState