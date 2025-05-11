package br.com.fitnesspro.scheduler.ui.screen.compromisse

import br.com.fitnesspro.model.enums.EnumSchedulerSituation.CANCELLED
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.CONFIRMED
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.ui.state.CompromiseUIState
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.tuple.PersonTuple
import java.time.OffsetDateTime
import java.util.UUID

internal val defaultPersonTuple = PersonTuple(
    id = "1",
    name = "Nikolas Luiz Schmitt",
    userType = EnumUserType.ACADEMY_MEMBER
)

internal val defaultCompromiseAcademyMemberState = CompromiseUIState(
    title = "Sugestão de Compromisso",
    subtitle = "01/05/2024",
    userType = EnumUserType.ACADEMY_MEMBER
)

internal val compromiseAcademyMemberEditionState = CompromiseUIState(
    title = "Compromisso",
    subtitle = "01/05/2024 08:00 às 09:00",
    userType = EnumUserType.ACADEMY_MEMBER,
    isEnabledDeleteButton = true,
    isEnabledMessageButton = true,
    toScheduler = TOScheduler(
        id = UUID.randomUUID().toString(),
        professionalName = "Gabriela da Silva",
        professionalType = EnumUserType.NUTRITIONIST,
        dateTimeStart = OffsetDateTime.now(),
        dateTimeEnd = OffsetDateTime.now().plusHours(1),
        situation = CONFIRMED,
        observation = "Muito bem observado"
    )
)

internal val compromiseAcademyMemberCancelatedState = CompromiseUIState(
    title = "Compromisso",
    subtitle = "01/05/2024 08:00 às 09:00",
    userType = EnumUserType.ACADEMY_MEMBER,
    isEnabledDeleteButton = true,
    isEnabledMessageButton = true,
    toScheduler = TOScheduler(
        id = UUID.randomUUID().toString(),
        professionalName = "Gabriela da Silva",
        professionalType = EnumUserType.NUTRITIONIST,
        dateTimeStart = OffsetDateTime.now(),
        dateTimeEnd = OffsetDateTime.now().plusHours(1),
        situation = CANCELLED,
        canceledDate = OffsetDateTime.now().plusHours(2),
        observation = "Muito bem observado"
    )
)

internal val compromisePersonalInclusionState = CompromiseUIState(
    title = "Novo Compromisso",
    subtitle = "01/05/2024",
    userType = EnumUserType.PERSONAL_TRAINER,
    toScheduler = TOScheduler(
        professionalName = "Gabriela da Silva",
        professionalType = EnumUserType.PERSONAL_TRAINER,
        dateTimeStart = OffsetDateTime.now(),
        dateTimeEnd = OffsetDateTime.now().plusHours(1),
        situation = CONFIRMED
    )
)

internal val compromisePersonalRecurrentState = CompromiseUIState(
    title = "Novo Compromisso Recorrente",
    subtitle = null,
    userType = EnumUserType.PERSONAL_TRAINER,
    recurrent = true
)