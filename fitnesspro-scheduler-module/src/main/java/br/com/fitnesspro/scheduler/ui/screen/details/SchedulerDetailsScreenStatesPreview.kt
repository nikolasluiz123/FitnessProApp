package br.com.fitnesspro.scheduler.ui.screen.details

import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.ui.state.SchedulerDetailsUIState
import br.com.fitnesspro.to.TOScheduler
import java.time.OffsetDateTime

internal val toSchedulerAcademyMember = TOScheduler(
    academyMemberName = "Josnei Cardoso Neto",
    professionalName = "Gabriela da Silva",
    professionalType = EnumUserType.NUTRITIONIST,
    dateTimeStart = OffsetDateTime.now(),
    dateTimeEnd = OffsetDateTime.now().plusHours(1),
    situation = EnumSchedulerSituation.SCHEDULED,
    compromiseType = EnumCompromiseType.FIRST
)

internal val academyMemberState = SchedulerDetailsUIState(
    title = "Detalhes dos Compromissos",
    subtitle = "01/05/2024",
    userType = EnumUserType.ACADEMY_MEMBER
)

internal val toSchedulerProfessional = TOScheduler(
    academyMemberName = "Josnei Cardoso Neto",
    professionalName = "Gabriela da Silva",
    dateTimeStart = OffsetDateTime.now(),
    dateTimeEnd = OffsetDateTime.now().plusHours(1),
    situation = EnumSchedulerSituation.SCHEDULED,
    compromiseType = EnumCompromiseType.FIRST
)

internal val professionalState = SchedulerDetailsUIState(
    title = "Detalhes dos Compromissos",
    subtitle = "01/05/2024",
    userType = EnumUserType.PERSONAL_TRAINER
)

internal val populatedListState = SchedulerDetailsUIState(
    title = "Detalhes dos Compromissos",
    subtitle = "01/05/2024",
    schedules = listOf(
        TOScheduler(
            academyMemberName = "Josnei Cardoso Neto",
            professionalName = "Gabriela da Silva",
            dateTimeStart = OffsetDateTime.now(),
            dateTimeEnd = OffsetDateTime.now().plusHours(1),
            situation = EnumSchedulerSituation.SCHEDULED,
            compromiseType = EnumCompromiseType.FIRST
        ),
        TOScheduler(
            academyMemberName = "Josnei Cardoso Neto",
            professionalName = "Gabriela da Silva",
            dateTimeStart = OffsetDateTime.now().plusHours(3),
            dateTimeEnd = OffsetDateTime.now().plusHours(4),
            situation = EnumSchedulerSituation.CONFIRMED,
            compromiseType = EnumCompromiseType.RECURRENT
        ),
        TOScheduler(
            academyMemberName = "Josnei Cardoso Neto",
            professionalName = "Gabriela da Silva",
            dateTimeStart = OffsetDateTime.now().plusHours(6),
            dateTimeEnd = OffsetDateTime.now().plusHours(7),
            situation = EnumSchedulerSituation.CANCELLED,
            canceledDate = OffsetDateTime.now().plusDays(1),
            compromiseType = EnumCompromiseType.RECURRENT
        )
    )
)