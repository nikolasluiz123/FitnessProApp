package br.com.fitnesspor.service.data.access.webclient.scheduler

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.scheduler.ISchedulerService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.mappers.SchedulerModelMapper
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
    private val schedulerService: ISchedulerService,
    private val schedulerModelMapper: SchedulerModelMapper
): FitnessProWebClient(context) {

    suspend fun saveScheduler(
        token: String,
        scheduler: Scheduler,
        schedulerType: String,
        dateStart: LocalDate? = null,
        dateEnd: LocalDate? = null,
        dayWeeks: List<DayOfWeek> = emptyList()
    ): PersistenceServiceResponse<SchedulerDTO> {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                val schedulerDTO = schedulerModelMapper.getSchedulerDTO(
                    scheduler = scheduler,
                    schedulerType = schedulerType,
                    dateStart = dateStart,
                    dateEnd = dateEnd,
                    dayWeeks = dayWeeks
                )

                schedulerService.saveScheduler(
                    token = formatToken(token),
                    schedulerDTO = schedulerDTO
                ).getResponseBody(SchedulerDTO::class.java)
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
                val listSchedulerDTO = schedulerList.map {
                    schedulerModelMapper.getSchedulerDTO(
                        scheduler = it,
                        schedulerType = schedulerType,
                        dateStart = null,
                        dateEnd = null,
                        dayWeeks = emptyList()
                    )
                }

                schedulerService.saveSchedulerBatch(
                    token = formatToken(token),
                    schedulerDTOList = listSchedulerDTO
                ).getResponseBody()
            }
        )
    }

    suspend fun saveSchedulerConfig(token: String, schedulerConfig: SchedulerConfig): PersistenceServiceResponse<SchedulerConfigDTO> {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                schedulerService.saveSchedulerConfig(
                    token = formatToken(token),
                    schedulerConfigDTO = schedulerModelMapper.getSchedulerConfigDTO(schedulerConfig),
                ).getResponseBody(SchedulerConfigDTO::class.java)
            }
        )
    }

    suspend fun saveSchedulerConfigBatch(token: String, schedulerConfigList: List<SchedulerConfig>): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                schedulerService.saveSchedulerConfigBatch(
                    token = token,
                    schedulerConfigDTOList = schedulerConfigList.map(schedulerModelMapper::getSchedulerConfigDTO)
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