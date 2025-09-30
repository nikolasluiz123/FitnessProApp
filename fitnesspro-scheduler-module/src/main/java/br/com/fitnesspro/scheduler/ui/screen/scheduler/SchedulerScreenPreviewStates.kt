package br.com.fitnesspro.scheduler.ui.screen.scheduler

import androidx.compose.ui.graphics.Color
import br.com.android.ui.compose.components.buttons.switchbutton.state.SwitchButtonField
import br.com.android.ui.compose.components.styles.LabelCalendarDayTextStyle
import br.com.fitnesspro.core.theme.RED_400
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.ui.screen.scheduler.components.DayStyle
import br.com.fitnesspro.scheduler.ui.state.SchedulerConfigUIState
import br.com.fitnesspro.scheduler.ui.state.SchedulerUIState
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser

internal val defaultSchedulerScreenState = SchedulerUIState(
    title = "Agenda"
)

internal val personalSchedulerScreenState = SchedulerUIState(
    title = "Agenda",
    isVisibleFabRecurrentScheduler = true
)

internal val defaultDayStyle = DayStyle(
    backgroundColor = RED_400,
    textStyle = LabelCalendarDayTextStyle,
    textColor = Color.White
)

internal val fakeTOPersonPersonal = TOPerson(
    user = TOUser(
        type = EnumUserType.PERSONAL_TRAINER
    )
)

internal val fakeTOPersonMember = TOPerson(
    user = TOUser(
        type = EnumUserType.ACADEMY_MEMBER
    )
)

internal val schedulerConfigPersonalPopulatedState = SchedulerConfigUIState(
    toPerson = fakeTOPersonPersonal,
    notification = SwitchButtonField(checked = true)
)

internal val schedulerConfigPersonalState = SchedulerConfigUIState(
    toPerson = fakeTOPersonPersonal
)

internal val schedulerConfigMemberState = SchedulerConfigUIState(
    toPerson = fakeTOPersonMember
)