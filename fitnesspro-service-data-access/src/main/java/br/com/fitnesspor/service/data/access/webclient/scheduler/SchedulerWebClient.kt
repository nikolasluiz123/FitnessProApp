package br.com.fitnesspor.service.data.access.webclient.scheduler

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.scheduler.ISchedulerService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.models.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.shared.communication.dtos.scheduler.RecurrentConfigDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerConfigDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerDTO
import br.com.fitnesspro.shared.communication.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import com.google.gson.GsonBuilder
import java.time.DayOfWeek
import java.time.LocalDate
import br.com.fitnesspro.models.scheduler.enums.EnumCompromiseType as EnumCompromiseTypeService
import br.com.fitnesspro.models.scheduler.enums.EnumSchedulerSituation as EnumSchedulerSituationService

class SchedulerWebClient(
    context: Context,
    private val schedulerService: ISchedulerService
): FitnessProWebClient(context) {

    suspend fun saveScheduler(
        token: String,
        scheduler: Scheduler,
        schedulerType: String,
        dateStart: LocalDate? = null,
        dateEnd: LocalDate? = null,
        dayWeeks: List<DayOfWeek> = emptyList()
    ): PersistenceServiceResponse {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                schedulerService.saveScheduler(
                    token = formatToken(token),
                    schedulerDTO = scheduler.toSchedulerDTO(
                        schedulerType = schedulerType,
                        dateStart = dateStart,
                        dateEnd = dateEnd,
                        dayWeeks = dayWeeks
                    )
                ).getResponseBody()
            }
        )
    }

    suspend fun saveSchedulerBatch(
        token: String,
        schedulerList: List<Scheduler>,
        schedulerType: String,
    ): PersistenceServiceResponse {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                schedulerService.saveSchedulerBatch(
                    token = formatToken(token),
                    schedulerDTOList = schedulerList.map {
                        it.toSchedulerDTO(
                            schedulerType = schedulerType,
                            dateStart = null,
                            dateEnd = null,
                            dayWeeks = emptyList(),
                        )
                    }
                ).getResponseBody()
            }
        )
    }

    suspend fun saveSchedulerConfig(schedulerConfig: SchedulerConfig): PersistenceServiceResponse {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                schedulerService.saveSchedulerConfig(
                    schedulerConfigDTO = schedulerConfig.toSchedulerConfigDTO()
                ).getResponseBody()
            }
        )
    }

    suspend fun saveSchedulerConfigBatch(token: String, schedulerConfigList: List<SchedulerConfig>): PersistenceServiceResponse {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                schedulerService.saveSchedulerConfigBatch(
                    token = token,
                    schedulerConfigDTOList = schedulerConfigList.map { it.toSchedulerConfigDTO() }
                ).getResponseBody()
            }
        )
    }

    suspend fun importSchedulerConfigs(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ReadServiceResponse<SchedulerConfigDTO> {
        return readServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                schedulerService.importScheduleConfigs(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody()
            }
        )
    }

    suspend fun importSchedulers(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ReadServiceResponse<SchedulerDTO> {
        return readServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                schedulerService.importSchedules(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody()
            }
        )
    }

    private fun SchedulerConfig.toSchedulerConfigDTO(): SchedulerConfigDTO {
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

    private fun Scheduler.toSchedulerDTO(
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

}