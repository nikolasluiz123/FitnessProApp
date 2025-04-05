package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.to.TOSchedulerConfig

fun SchedulerConfig.toTOSchedulerConfig(): TOSchedulerConfig {
    return TOSchedulerConfig(
        id = id,
        personId = personId,
        alarm = alarm,
        notification = notification,
        minScheduleDensity = minScheduleDensity,
        maxScheduleDensity = maxScheduleDensity,
    )
}

fun TOSchedulerConfig.toSchedulerConfig(): SchedulerConfig {
    val model = SchedulerConfig(
        personId = personId,
        alarm = alarm,
        notification = notification,
        minScheduleDensity = minScheduleDensity!!,
        maxScheduleDensity = maxScheduleDensity!!,
    )

    id?.let { model.id = it }

    return model
}

fun Scheduler.toTOScheduler(memberPersonName: String, professionalPersonName: String, professionalUserType: EnumUserType): TOScheduler {
    return TOScheduler(
        id = id,
        academyMemberPersonId = academyMemberPersonId,
        academyMemberName = memberPersonName,
        professionalPersonId = professionalPersonId,
        professionalName = professionalPersonName,
        professionalType = professionalUserType,
        scheduledDate = scheduledDate,
        start = start,
        end = end,
        canceledDate = canceledDate,
        situation = situation,
        compromiseType = compromiseType,
        observation = observation,
        active = active,
    )
}

fun TOScheduler.toScheduler(): Scheduler {
    val model = Scheduler(
        academyMemberPersonId = academyMemberPersonId,
        professionalPersonId = professionalPersonId,
        scheduledDate = scheduledDate,
        start = start,
        end = end,
        canceledDate = canceledDate,
        situation = situation,
        compromiseType = compromiseType,
        observation = observation,
        active = active,
    )

    id?.let { model.id = it }

    return model
}