package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.shared.communication.dtos.scheduler.RecurrentConfigDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerConfigDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerDTO
import br.com.fitnesspro.shared.communication.enums.scheduler.EnumSchedulerType
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.to.TOSchedulerConfig
import java.time.DayOfWeek
import java.time.LocalDate
import br.com.fitnesspro.shared.communication.enums.scheduler.EnumCompromiseType as EnumCompromiseTypeService
import br.com.fitnesspro.shared.communication.enums.scheduler.EnumSchedulerSituation as EnumSchedulerSituationService

fun SchedulerConfig.getTOSchedulerConfig(): TOSchedulerConfig {
    return TOSchedulerConfig(
        id = id,
        notification = notification,
        notificationAntecedenceTime = notificationAntecedenceTime,
        minScheduleDensity = minScheduleDensity,
        maxScheduleDensity = maxScheduleDensity,
        personId = personId,
    )
}

fun TOSchedulerConfig.getSchedulerConfig(): SchedulerConfig {
    val model = SchedulerConfig(
        notification = notification,
        notificationAntecedenceTime = notificationAntecedenceTime!!,
        minScheduleDensity = minScheduleDensity!!,
        maxScheduleDensity = maxScheduleDensity!!,
        personId = personId,
    )

    id?.let { model.id = it }

    return model
}

fun SchedulerConfigDTO.getSchedulerConfig(): SchedulerConfig {
    val model = SchedulerConfig(
        notification = notification,
        notificationAntecedenceTime = notificationAntecedenceTime,
        minScheduleDensity = minScheduleDensity,
        maxScheduleDensity = maxScheduleDensity,
        personId = personId,
        transmissionState = EnumTransmissionState.TRANSMITTED,
    )

    id?.let { model.id = it }

    return model
}

fun SchedulerConfig.getSchedulerConfigDTO(): SchedulerConfigDTO {
    return SchedulerConfigDTO(
        id = id,
        notification = notification,
        notificationAntecedenceTime = notificationAntecedenceTime,
        minScheduleDensity = minScheduleDensity,
        maxScheduleDensity = maxScheduleDensity,
        personId = personId,
    )
}

fun Scheduler.getTOScheduler(
    memberPersonName: String,
    professionalPersonName: String,
    cancellationPersonName: String?,
    professionalUserType: EnumUserType
): TOScheduler {
    return TOScheduler(
        id = id,
        academyMemberPersonId = academyMemberPersonId,
        academyMemberName = memberPersonName,
        professionalPersonId = professionalPersonId,
        professionalName = professionalPersonName,
        professionalType = professionalUserType,
        dateTimeStart = dateTimeStart,
        dateTimeEnd = dateTimeEnd,
        canceledDate = canceledDate,
        cancellationPersonId = cancellationPersonId,
        cancellationPersonName = cancellationPersonName,
        situation = situation,
        compromiseType = compromiseType,
        observation = observation,
        active = active,
    )
}

fun TOScheduler.getScheduler(): Scheduler {
    val model = Scheduler(
        academyMemberPersonId = academyMemberPersonId,
        professionalPersonId = professionalPersonId,
        dateTimeStart = dateTimeStart,
        dateTimeEnd = dateTimeEnd,
        canceledDate = canceledDate,
        cancellationPersonId = cancellationPersonId,
        situation = situation,
        compromiseType = compromiseType,
        observation = observation,
        active = active,
    )

    id?.let { model.id = it }

    return model
}

fun SchedulerDTO.getScheduler(): Scheduler {
    return Scheduler(
        id = id!!,
        academyMemberPersonId = academyMemberPersonId,
        professionalPersonId = professionalPersonId,
        dateTimeStart = dateTimeStart,
        dateTimeEnd = dateTimeEnd,
        canceledDate = canceledDate,
        cancellationPersonId = cancellationPersonId,
        situation = getSchedulerSituation(situation!!),
        compromiseType = getCompromiseType(compromiseType!!),
        observation = observation,
        active = active,
        transmissionState = EnumTransmissionState.TRANSMITTED
    )
}

fun Scheduler.getSchedulerDTO(
    schedulerType: String,
    dateStart: LocalDate?,
    dateEnd: LocalDate?,
    dayWeeks: List<DayOfWeek>
): SchedulerDTO {
    val enumSchedulerType = EnumSchedulerType.valueOf(schedulerType)

    return SchedulerDTO(
        id = if (enumSchedulerType == EnumSchedulerType.RECURRENT) null else id,
        academyMemberPersonId = academyMemberPersonId,
        professionalPersonId = professionalPersonId,
        dateTimeStart = dateTimeStart,
        dateTimeEnd = dateTimeEnd,
        canceledDate = canceledDate,
        cancellationPersonId = cancellationPersonId,
        situation = getServiceSchedulerSituation(situation!!),
        compromiseType = getServiceCompromiseType(compromiseType!!),
        observation = observation,
        active = active,
        type = enumSchedulerType,
        recurrentConfig = getRecurrentConfigDTO(
            schedulerType = enumSchedulerType,
            dateStart = dateStart,
            dateEnd = dateEnd,
            dayWeeks = dayWeeks
        ),
    )
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

private fun getServiceCompromiseType(compromiseType: EnumCompromiseType): EnumCompromiseTypeService {
    return when (compromiseType) {
        EnumCompromiseType.FIRST -> EnumCompromiseTypeService.FIRST
        EnumCompromiseType.RECURRENT -> EnumCompromiseTypeService.RECURRENT
    }
}

private fun getCompromiseType(compromiseType: EnumCompromiseTypeService): EnumCompromiseType {
    return when (compromiseType) {
        EnumCompromiseTypeService.FIRST -> EnumCompromiseType.FIRST
        EnumCompromiseTypeService.RECURRENT -> EnumCompromiseType.RECURRENT
    }
}

private fun getServiceSchedulerSituation(situation: EnumSchedulerSituation): EnumSchedulerSituationService {
    return when (situation) {
        EnumSchedulerSituation.SCHEDULED -> EnumSchedulerSituationService.SCHEDULED
        EnumSchedulerSituation.CANCELLED -> EnumSchedulerSituationService.CANCELLED
        EnumSchedulerSituation.CONFIRMED -> EnumSchedulerSituationService.CONFIRMED
        EnumSchedulerSituation.COMPLETED -> EnumSchedulerSituationService.COMPLETED
    }
}

private fun getSchedulerSituation(situation: EnumSchedulerSituationService): EnumSchedulerSituation {
    return when (situation) {
        EnumSchedulerSituationService.SCHEDULED -> EnumSchedulerSituation.SCHEDULED
        EnumSchedulerSituationService.CANCELLED -> EnumSchedulerSituation.CANCELLED
        EnumSchedulerSituationService.CONFIRMED -> EnumSchedulerSituation.CONFIRMED
        EnumSchedulerSituationService.COMPLETED -> EnumSchedulerSituation.COMPLETED
    }
}