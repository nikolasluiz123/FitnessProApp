package br.com.fitnesspor.service.data.access.webclient.scheduler

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.mappers.toSchedulerConfigDTO
import br.com.fitnesspor.service.data.access.mappers.toSchedulerDTO
import br.com.fitnesspor.service.data.access.service.scheduler.ISchedulerService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerConfigDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import com.google.gson.GsonBuilder
import java.time.DayOfWeek
import java.time.LocalDate

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
    ): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
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

    suspend fun saveSchedulerConfigBatch(token: String, schedulerConfigList: List<SchedulerConfig>): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
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
    ): ImportationServiceResponse<SchedulerConfigDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                schedulerService.importScheduleConfigs(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(SchedulerConfigDTO::class.java)
            }
        )
    }

    suspend fun importSchedulers(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<SchedulerDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                schedulerService.importSchedules(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(SchedulerDTO::class.java)
            }
        )
    }

}