package br.com.fitnesspro.service.data.access.webclient.scheduler

import android.content.Context
import br.com.fitnesspro.service.data.access.extensions.getResponseBody
import br.com.fitnesspro.service.data.access.service.scheduler.ISchedulerService
import br.com.fitnesspro.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.mappers.getSchedulerDTO
import br.com.fitnesspro.mappers.getWorkoutDTO
import br.com.fitnesspro.mappers.getWorkoutGroupDTO
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.shared.communication.dtos.scheduler.RecurrentSchedulerDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerDTO
import br.com.fitnesspro.shared.communication.enums.scheduler.EnumSchedulerType
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse

class SchedulerWebClient(
    context: Context,
    private val schedulerService: ISchedulerService,
): FitnessProWebClient(context) {

    suspend fun saveScheduler(
        token: String,
        scheduler: Scheduler,
        schedulerType: String
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
}