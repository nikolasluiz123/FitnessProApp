package br.com.fitnesspor.service.data.access.webclient.workout

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.workout.IWorkoutService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.mappers.getWorkoutDTO
import br.com.fitnesspro.mappers.getWorkoutGroupDTO
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.WorkoutModuleImportFilter
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import com.google.gson.GsonBuilder

class WorkoutWebClient(
    context: Context,
    private val workoutService: IWorkoutService,
): FitnessProWebClient(context) {

    suspend fun saveWorkout(token: String, workout: Workout) {
        persistenceServiceErrorHandlingBlock(
            codeBlock = {
                workoutService.saveWorkout(
                    token = formatToken(token),
                    workoutDTO = workout.getWorkoutDTO()
                ).getResponseBody(WorkoutDTO::class.java)
            }
        )
    }

    suspend fun saveWorkoutBatch(
        token: String,
        workoutList: List<Workout>,
    ): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                val listDTO = workoutList.map(Workout::getWorkoutDTO)

                workoutService.saveWorkoutBatch(
                    token = formatToken(token),
                    workoutDTOList = listDTO
                ).getResponseBody()
            }
        )
    }

    suspend fun saveWorkoutGroup(token: String, workoutGroup: WorkoutGroup): PersistenceServiceResponse<WorkoutGroupDTO> {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                workoutService.saveWorkoutGroup(
                    token = formatToken(token),
                    workoutGroupDTO = workoutGroup.getWorkoutGroupDTO()
                ).getResponseBody(WorkoutGroupDTO::class.java)
            }
        )
    }

    suspend fun saveWorkoutGroupBatch(
        token: String,
        workoutGroupList: List<WorkoutGroup>,
    ): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                val listDTO = workoutGroupList.map(WorkoutGroup::getWorkoutGroupDTO)

                workoutService.saveWorkoutGroupBatch(
                    token = formatToken(token),
                    workoutGroupDTOList = listDTO
                ).getResponseBody()
            }
        )
    }

    suspend fun importWorkouts(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<WorkoutDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                workoutService.importWorkout(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(WorkoutDTO::class.java)
            }
        )
    }

    suspend fun importWorkoutGroups(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<WorkoutGroupDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                workoutService.importWorkoutGroup(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(WorkoutGroupDTO::class.java)
            }
        )
    }

    suspend fun inactivateWorkoutGroup(
        token: String,
        workoutGroupId: String
    ): FitnessProServiceResponse {
        return serviceErrorHandlingBlock(
            codeBlock = {
                workoutService.inactivateWorkoutGroup(
                    token = formatToken(token),
                    workoutGroupId = workoutGroupId
                ).getResponseBody()
            }
        )
    }
}