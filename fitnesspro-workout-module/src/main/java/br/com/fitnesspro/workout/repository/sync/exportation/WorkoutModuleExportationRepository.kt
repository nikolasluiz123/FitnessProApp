package br.com.fitnesspro.workout.repository.sync.exportation

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.mappers.getExerciseDTO
import br.com.fitnesspro.mappers.getExerciseExecutionDTO
import br.com.fitnesspro.mappers.getExercisePreDefinitionDTO
import br.com.fitnesspro.mappers.getVideoDTO
import br.com.fitnesspro.mappers.getVideoExerciseDTO
import br.com.fitnesspro.mappers.getVideoExerciseExecutionDTO
import br.com.fitnesspro.mappers.getVideoExercisePreDefinitionDTO
import br.com.fitnesspro.mappers.getWorkoutDTO
import br.com.fitnesspro.mappers.getWorkoutGroupDTO
import br.com.fitnesspro.mappers.getWorkoutGroupPreDefinitionDTO
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.model.workout.VideoExercise
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.VideoExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition
import br.com.fitnesspro.shared.communication.dtos.sync.WorkoutModuleSyncDTO
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.workout.injection.IWorkoutModuleSyncRepositoryEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlin.reflect.KClass

class WorkoutModuleExportationRepository(
    context: Context,
    private val personRepository: PersonRepository
): AbstractExportationRepository<WorkoutModuleSyncDTO>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IWorkoutModuleSyncRepositoryEntryPoint::class.java)

    override suspend fun getExportationData(pageInfos: ExportPageInfos): Map<KClass<out IntegratedModel>, List<IntegratedModel>> {
        val map = mutableMapOf<KClass<out IntegratedModel>, List<IntegratedModel>>()
        val personId = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!).id

        map.put(Workout::class, entryPoint.getWorkoutDAO().getExportationData(pageInfos, personId))
        map.put(WorkoutGroup::class, entryPoint.getWorkoutGroupDAO().getExportationData(pageInfos, personId))
        map.put(Exercise::class, entryPoint.getExerciseDAO().getExportationData(pageInfos, personId))
        map.put(Video::class, entryPoint.getVideoDAO().getExportationData(pageInfos))
        map.put(VideoExercise::class, entryPoint.getVideoExerciseDAO().getExportationData(pageInfos, personId))
        map.put(ExerciseExecution::class, entryPoint.getExerciseExecutionDAO().getExportationData(pageInfos, personId))
        map.put(VideoExerciseExecution::class, entryPoint.getVideoExerciseExecutionDAO().getExportationData(pageInfos, personId))
        map.put(WorkoutGroupPreDefinition::class, entryPoint.getWorkoutGroupPreDefinitionDAO().getExportationData(pageInfos, personId))
        map.put(ExercisePreDefinition::class, entryPoint.getExercisePreDefinitionDAO().getExportationData(pageInfos, personId))
        map.put(VideoExercisePreDefinition::class, entryPoint.getVideoExercisePreDefinitionDAO().getExportationData(pageInfos, personId))

        return map
    }

    override suspend fun getExportationDTO(models: Map<KClass<out IntegratedModel>, List<IntegratedModel>>): WorkoutModuleSyncDTO {
        val workouts = models[Workout::class]!!.map { workout ->
            workout as Workout
            workout.getWorkoutDTO()
        }

        val workoutGroups = models[WorkoutGroup::class]!!.map { workoutGroup ->
            workoutGroup as WorkoutGroup
            workoutGroup.getWorkoutGroupDTO()
        }

        val exercises = models[Exercise::class]!!.map { exercise ->
            exercise as Exercise

            val workoutGroupDTO = workoutGroups.firstOrNull {
                it.id == exercise.workoutGroupId
            } ?: entryPoint.getWorkoutGroupDAO().findById(exercise.workoutGroupId)!!.getWorkoutGroupDTO()

            exercise.getExerciseDTO(workoutGroupDTO)
        }

        val videos = models[Video::class]!!.map { video ->
            video as Video
            video.getVideoDTO()
        }

        val videoExercises = models[VideoExercise::class]!!.map { videoExercise ->
            videoExercise as VideoExercise
            videoExercise.getVideoExerciseDTO()
        }

        val exerciseExecutions = models[ExerciseExecution::class]!!.map { exerciseExecution ->
            exerciseExecution as ExerciseExecution
            exerciseExecution.getExerciseExecutionDTO()
        }

        val videoExerciseExecutions = models[VideoExerciseExecution::class]!!.map { videoExerciseExecution ->
            videoExerciseExecution as VideoExerciseExecution
            videoExerciseExecution.getVideoExerciseExecutionDTO()
        }

        val workoutGroupPreDefinitions = models[WorkoutGroupPreDefinition::class]!!.map { workoutGroupPreDefinition ->
            workoutGroupPreDefinition as WorkoutGroupPreDefinition
            workoutGroupPreDefinition.getWorkoutGroupPreDefinitionDTO()
        }

        val exercisePreDefinitions = models[ExercisePreDefinition::class]!!.map { exercisePreDefinition ->
            exercisePreDefinition as ExercisePreDefinition
            exercisePreDefinition.getExercisePreDefinitionDTO()
        }

        val videoExercisePreDefinitions = models[VideoExercisePreDefinition::class]!!.map { videoExercisePreDefinition ->
            videoExercisePreDefinition as VideoExercisePreDefinition
            videoExercisePreDefinition.getVideoExercisePreDefinitionDTO()
        }

        return WorkoutModuleSyncDTO(
            workouts = workouts,
            workoutGroups = workoutGroups,
            exercises = exercises,
            videos = videos,
            videoExercises = videoExercises,
            exerciseExecutions = exerciseExecutions,
            videoExerciseExecutions = videoExerciseExecutions,
            workoutGroupsPreDefinitions = workoutGroupPreDefinitions,
            exercisePredefinitions = exercisePreDefinitions,
            videoExercisePreDefinitions = videoExercisePreDefinitions
        )
    }

    override suspend fun callExportationService(
        dto: WorkoutModuleSyncDTO,
        token: String
    ): ExportationServiceResponse {
        return entryPoint.getWorkoutSyncWebClient().export(token, dto)
    }

    override fun getIntegratedMaintenanceDAO(modelClass: KClass<out IntegratedModel>): IntegratedMaintenanceDAO<out IntegratedModel> {
        return when (modelClass) {
            Workout::class -> entryPoint.getWorkoutDAO()
            WorkoutGroup::class -> entryPoint.getWorkoutGroupDAO()
            Exercise::class -> entryPoint.getExerciseDAO()
            Video::class -> entryPoint.getVideoDAO()
            VideoExercise::class -> entryPoint.getVideoExerciseDAO()
            ExerciseExecution::class -> entryPoint.getExerciseExecutionDAO()
            VideoExerciseExecution::class -> entryPoint.getVideoExerciseExecutionDAO()
            WorkoutGroupPreDefinition::class -> entryPoint.getWorkoutGroupPreDefinitionDAO()
            ExercisePreDefinition::class -> entryPoint.getExercisePreDefinitionDAO()
            VideoExercisePreDefinition::class -> entryPoint.getVideoExercisePreDefinitionDAO()
            else -> throw IllegalArgumentException("Não foi possível recuperar o DAO. Classe de modelo inválida.")
        }
    }
}