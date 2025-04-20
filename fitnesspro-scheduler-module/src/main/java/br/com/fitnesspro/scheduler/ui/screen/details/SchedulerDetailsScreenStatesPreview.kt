package br.com.fitnesspro.scheduler.ui.screen.details

import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.ui.state.SchedulerDetailsUIState
import br.com.fitnesspro.to.TOScheduler
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

internal val toSchedulerAcademyMember = TOScheduler(
    academyMemberName = "Josnei Cardoso Neto",
    professionalName = "Gabriela da Silva",
    scheduledDate = LocalDate.parse("2024-05-01"),
    timeStart = LocalTime.parse("08:00"),
    timeEnd = LocalTime.parse("09:00"),
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
    scheduledDate = LocalDate.parse("2024-05-01"),
    timeStart = LocalTime.parse("08:00"),
    timeEnd = LocalTime.parse("09:00"),
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
            scheduledDate = LocalDate.parse("2024-05-01"),
            timeStart = LocalTime.parse("08:00"),
            timeEnd = LocalTime.parse("09:00"),
            situation = EnumSchedulerSituation.SCHEDULED,
            compromiseType = EnumCompromiseType.FIRST
        ),
        TOScheduler(
            academyMemberName = "Josnei Cardoso Neto",
            professionalName = "Gabriela da Silva",
            scheduledDate = LocalDate.parse("2024-05-01"),
            timeStart = LocalTime.parse("12:00"),
            timeEnd = LocalTime.parse("12:30"),
            situation = EnumSchedulerSituation.CONFIRMED,
            compromiseType = EnumCompromiseType.RECURRENT
        ),
        TOScheduler(
            academyMemberName = "Josnei Cardoso Neto",
            professionalName = "Gabriela da Silva",
            scheduledDate = LocalDate.parse("2024-05-01"),
            timeStart = LocalTime.parse("15:00"),
            timeEnd = LocalTime.parse("16:30"),
            situation = EnumSchedulerSituation.CANCELLED,
            canceledDate = LocalDateTime.parse("2024-05-01T10:15:30"),
            compromiseType = EnumCompromiseType.RECURRENT
        )
    )
)