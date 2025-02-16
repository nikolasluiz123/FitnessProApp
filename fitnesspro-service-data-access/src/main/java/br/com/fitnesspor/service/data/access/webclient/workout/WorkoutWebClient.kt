package br.com.fitnesspor.service.data.access.webclient.workout

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.workout.IWorkoutService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupDTO

class WorkoutWebClient(
    context: Context,
    private val workoutService: IWorkoutService
): FitnessProWebClient(context) {

    suspend fun saveWorkout(token: String, workout: Workout) {
        persistenceServiceErrorHandlingBlock(
            codeBlock = {
                workoutService.saveWorkout(
                    token = formatToken(token),
                    workoutDTO = workout.toWorkoutDTO()
                ).getResponseBody()
            }
        )
    }

    suspend fun saveWorkoutGroup(token: String, workoutGroup: WorkoutGroup) {
        persistenceServiceErrorHandlingBlock(
            codeBlock = {
                workoutService.saveWorkoutGroup(
                    token = formatToken(token),
                    workoutGroupDTO = workoutGroup.toWorkoutGroupDTO()
                ).getResponseBody()
            }
        )
    }

    private fun Workout.toWorkoutDTO(): WorkoutDTO {
        return WorkoutDTO(
            id = id,
            active = active,
            academyMemberPersonId = academyMemberPersonId,
            professionalPersonId = professionalPersonId,
            dateStart = start,
            dateEnd = end
        )
    }

    private fun WorkoutGroup.toWorkoutGroupDTO(): WorkoutGroupDTO {
        return WorkoutGroupDTO(
            id = id,
            active = active,
            workoutId = workoutId,
            dayWeek = dayWeek,
            name = name
        )
    }
}