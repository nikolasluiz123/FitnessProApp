package br.com.fitnesspor.service.data.access.webclient.scheduler

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.scheduler.ISchedulerService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.mappers.getSchedulerConfigDTO
import br.com.fitnesspro.mappers.getSchedulerDTO
import br.com.fitnesspro.mappers.getWorkoutDTO
import br.com.fitnesspro.mappers.getWorkoutGroupDTO
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.shared.communication.dtos.scheduler.RecurrentSchedulerDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerConfigDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerDTO
import br.com.fitnesspro.shared.communication.enums.scheduler.EnumSchedulerType
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import com.google.gson.GsonBuilder
import java.time.DayOfWeek
import java.time.LocalDate

class SchedulerWebClient(
    context: Context,
    private val schedulerService: ISchedulerService,
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
                val schedulerDTO = scheduler.getSchedulerDTO(
                    schedulerType = schedulerType
                )

                schedulerService.saveScheduler(
                    token = formatToken(token),
                    schedulerDTO = schedulerDTO
                ).getResponseBody(SchedulerDTO::class.java)
            }
        )
    }

    suspend fun saveRecurrentScheduler(
        token: String,
        schedules: List<Scheduler>,
        workout: Workout,
        workoutGroups: List<WorkoutGroup>
    ): FitnessProServiceResponse {
        return serviceErrorHandlingBlock(
            codeBlock = {
                val dto = RecurrentSchedulerDTO(
                    schedules = schedules.map {
                        it.getSchedulerDTO(EnumSchedulerType.RECURRENT.name)
                    },
                    workoutDTO = workout.getWorkoutDTO(),
                    workoutGroups = workoutGroups.map { it.getWorkoutGroupDTO() }
                )

                schedulerService.saveRecurrentScheduler(
                    token = formatToken(token),
                    recurrentSchedulerDTO = dto
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
                val listSchedulerDTO = schedulerList.map {
                    it.getSchedulerDTO(
                        schedulerType = schedulerType
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
                    schedulerConfigDTO = schedulerConfig.getSchedulerConfigDTO(),
                ).getResponseBody(SchedulerConfigDTO::class.java)
            }
        )
    }

    suspend fun saveSchedulerConfigBatch(token: String, schedulerConfigList: List<SchedulerConfig>): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                schedulerService.saveSchedulerConfigBatch(
                    token = token,
                    schedulerConfigDTOList = schedulerConfigList.map(SchedulerConfig::getSchedulerConfigDTO)
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