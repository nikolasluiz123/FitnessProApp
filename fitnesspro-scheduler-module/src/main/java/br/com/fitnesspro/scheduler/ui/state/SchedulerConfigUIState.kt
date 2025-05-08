package br.com.fitnesspro.scheduler.ui.state

import br.com.fitnesspro.compose.components.fields.state.SwitchButtonField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOSchedulerConfig

data class SchedulerConfigUIState(
    val notification: SwitchButtonField = SwitchButtonField(),
    val minEventDensity: TextField = TextField(),
    val maxEventDensity: TextField = TextField(),
    val toPerson: TOPerson? = null,
    val toConfig: TOSchedulerConfig = TOSchedulerConfig(),
    val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { }
): ILoadingUIState