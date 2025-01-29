package br.com.fitnesspro.scheduler.ui.state

import br.com.fitnesspro.compose.components.fields.state.SwitchButtonField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOSchedulerConfig

data class SchedulerConfigUIState(
    val alarm: SwitchButtonField = SwitchButtonField(),
    val notification: SwitchButtonField = SwitchButtonField(),
    val minEventDensity: TextField = TextField(),
    val maxEventDensity: TextField = TextField(),
    val userType: EnumUserType? = null,
    val toConfig: TOSchedulerConfig = TOSchedulerConfig(),
    val messageDialogState: MessageDialogState = MessageDialogState()
)