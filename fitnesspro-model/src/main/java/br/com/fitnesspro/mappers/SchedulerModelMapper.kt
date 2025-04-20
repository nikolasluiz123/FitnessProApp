package br.com.fitnesspro.mappers

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

class SchedulerModelMapper: AbstractModelMapper() {

    fun getTOSchedulerConfig(schedulerConfig: SchedulerConfig): TOSchedulerConfig {
        return mapper.map(schedulerConfig, TOSchedulerConfig::class.java)
    }

    fun getSchedulerConfig(toSchedulerConfig: TOSchedulerConfig): SchedulerConfig {
        return mapper.map(toSchedulerConfig, SchedulerConfig::class.java).apply {
            toSchedulerConfig.id?.let { id = it }
        }
    }

    fun getSchedulerConfig(schedulerConfigDTO: SchedulerConfigDTO): SchedulerConfig {
        return mapper.map(schedulerConfigDTO, SchedulerConfig::class.java)
    }

    fun getSchedulerConfigDTO(schedulerConfig: SchedulerConfig): SchedulerConfigDTO {
        return mapper.map(schedulerConfig, SchedulerConfigDTO::class.java).copy(active = true)
    }

    fun getTOScheduler(
        scheduler: Scheduler,
        memberPersonName: String,
        professionalPersonName: String,
        professionalUserType: EnumUserType
    ): TOScheduler {
        return mapper.map(scheduler, TOScheduler::class.java).copy(
            academyMemberName = memberPersonName,
            professionalName = professionalPersonName,
            professionalType = professionalUserType
        )
    }

    fun getScheduler(toScheduler: TOScheduler): Scheduler {
        return mapper.map(toScheduler, Scheduler::class.java).apply {
            toScheduler.id?.let { id = it }
        }
    }

    fun getScheduler(schedulerDTO: SchedulerDTO): Scheduler {
        return mapper.map(schedulerDTO, Scheduler::class.java).apply {
            schedulerDTO.id?.let { id = it }
        }
    }

    fun getSchedulerDTO(
        scheduler: Scheduler,
        schedulerType: String,
        dateStart: LocalDate?,
        dateEnd: LocalDate?,
        dayWeeks: List<DayOfWeek>
    ): SchedulerDTO {
        return mapper.map(scheduler, SchedulerDTO::class.java).copy(
            recurrentConfig = getRecurrentConfigDTO(
                schedulerType = EnumSchedulerType.valueOf(schedulerType),
                dateStart = dateStart,
                dateEnd = dateEnd,
                dayWeeks = dayWeeks
            )
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
}