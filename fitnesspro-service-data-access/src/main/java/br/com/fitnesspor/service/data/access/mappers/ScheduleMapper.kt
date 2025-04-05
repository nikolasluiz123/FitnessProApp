package br.com.fitnesspor.service.data.access.mappers

import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.models.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.shared.communication.dtos.scheduler.RecurrentConfigDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerConfigDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerDTO
import java.time.DayOfWeek
import java.time.LocalDate
import br.com.fitnesspro.models.scheduler.enums.EnumCompromiseType as EnumCompromiseTypeService
import br.com.fitnesspro.models.scheduler.enums.EnumSchedulerSituation as EnumSchedulerSituationService


fun SchedulerConfig.toSchedulerConfigDTO(): SchedulerConfigDTO {
    return SchedulerConfigDTO(
        id = id,
        active = true,
        alarm = alarm,
        notification = notification,
        minScheduleDensity = minScheduleDensity,
        maxScheduleDensity = maxScheduleDensity,
        personId = personId
    )
}

fun Scheduler.toSchedulerDTO(
    schedulerType: String,
    dateStart: LocalDate?,
    dateEnd: LocalDate?,
    dayWeeks: List<DayOfWeek>
): SchedulerDTO {
    val type = getSchedulerTypeWithName(schedulerType)

    return SchedulerDTO(
        id = id,
        active = active,
        academyMemberPersonId = academyMemberPersonId,
        professionalPersonId = professionalPersonId,
        scheduledDate = scheduledDate,
        situation = getSituation(situation!!),
        timeStart = start,
        timeEnd = end,
        canceledDate = canceledDate,
        compromiseType = getCompromiseType(compromiseType!!),
        observation = observation,
        type = type,
        recurrentConfig = getRecurrentConfigDTO(type, dateStart, dateEnd, dayWeeks)
    )
}

private fun getSchedulerTypeWithName(schedulerType: String): EnumSchedulerType {
    return EnumSchedulerType.entries.first { it.name == schedulerType }
}

private fun getRecurrentConfigDTO(
    schedulerType: EnumSchedulerType,
    dateStart: LocalDate?,
    dateEnd: LocalDate?,
    dayWeeks: List<DayOfWeek>
): RecurrentConfigDTO? {
    return if (schedulerType == EnumSchedulerType.RECURRENT) {
        RecurrentConfigDTO(
            dateStart = dateStart!!,
            dateEnd = dateEnd!!,
            dayWeeks = dayWeeks
        )
    } else {
        null
    }
}

private fun getCompromiseType(compromiseType: EnumCompromiseType): EnumCompromiseTypeService {
    return when (compromiseType) {
        EnumCompromiseType.FIRST -> EnumCompromiseTypeService.FIRST
        EnumCompromiseType.RECURRENT -> EnumCompromiseTypeService.RECURRENT
    }
}

private fun getSituation(situation: EnumSchedulerSituation): EnumSchedulerSituationService {
    return when (situation) {
        EnumSchedulerSituation.SCHEDULED -> EnumSchedulerSituationService.SCHEDULED
        EnumSchedulerSituation.CANCELLED -> EnumSchedulerSituationService.CANCELLED
        EnumSchedulerSituation.CONFIRMED -> EnumSchedulerSituationService.CONFIRMED
        EnumSchedulerSituation.COMPLETED -> EnumSchedulerSituationService.COMPLETED
    }
}