package br.com.fitnesspro.scheduler.ui.screen.scheduler

import androidx.compose.ui.graphics.Color
import br.com.fitnesspro.compose.components.fields.state.SwitchButtonField
import br.com.fitnesspro.core.theme.LabelCalendarDayTextStyle
import br.com.fitnesspro.core.theme.RED_400
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.ui.screen.scheduler.components.DayStyle
import br.com.fitnesspro.scheduler.ui.state.SchedulerConfigUIState
import br.com.fitnesspro.scheduler.ui.state.SchedulerUIState

internal val defaultSchedulerScreenState = SchedulerUIState(
    title = "Agenda"
)

internal val defaultDayStyle = DayStyle(
    backgroundColor = RED_400,
    textStyle = LabelCalendarDayTextStyle,
    textColor = Color.White
)

internal val schedulerConfigPersonalPopulatedState = SchedulerConfigUIState(
    userType = EnumUserType.PERSONAL_TRAINER,
    alarm = SwitchButtonField(checked = true),
    notification = SwitchButtonField(checked = true)
)

internal val schedulerConfigPersonalState = SchedulerConfigUIState(
    userType = EnumUserType.PERSONAL_TRAINER
)

internal val schedulerConfigMemberState = SchedulerConfigUIState(
    userType = EnumUserType.ACADEMY_MEMBER
)