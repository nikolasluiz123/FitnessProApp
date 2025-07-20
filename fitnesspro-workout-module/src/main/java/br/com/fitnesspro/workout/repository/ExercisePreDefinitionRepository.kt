package br.com.fitnesspro.workout.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ExercisePreDefinitionDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupPreDefinitionDAO
import br.com.fitnesspro.mappers.getExercisePreDefinition
import br.com.fitnesspro.mappers.getTOExercisePreDefinition
import br.com.fitnesspro.mappers.getWorkoutGroupPreDefinition
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOExercisePreDefinition
import br.com.fitnesspro.to.TOVideoExercisePreDefinition
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition

class ExercisePreDefinitionRepository(
    context: Context,
    private val exercisePreDefinitionDAO: ExercisePreDefinitionDAO,
    private val workoutGroupPreDefinitionDAO: WorkoutGroupPreDefinitionDAO,
    private val videoRepository: VideoRepository
): FitnessProRepository(context) {

    fun getExercisesAndPreDefinitions(
        workoutId: String,
        authenticatedPersonId: String,
        simpleFilter: String
    ): Pager<Int, TOExercise> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                exercisePreDefinitionDAO.getExercisesAndPreDefinitions(
                    authenticatedPersonId = authenticatedPersonId,
                    simpleFilter = simpleFilter,
                    workoutId = workoutId
                )
            }
        )
    }

    suspend fun findExercisePreDefinitionByName(name: String): TOExercisePreDefinition? {
        return exercisePreDefinitionDAO.findExercisePreDefinitionByName(name)?.getTOExercisePreDefinition()
    }

    suspend fun saveExercisePreDefinitionLocally(
        toExercisePreDefinition: TOExercisePreDefinition,
        toVideos: List<TOVideoExercisePreDefinition>
    ) {
        val exercisePreDefinition = toExercisePreDefinition.getExercisePreDefinition()

        if (toExercisePreDefinition.id == null) {
            exercisePreDefinitionDAO.insert(exercisePreDefinition)
        } else {
            exercisePreDefinitionDAO.update(exercisePreDefinition)
        }

        toExercisePreDefinition.id = exercisePreDefinition.id
        toVideos.forEach { it.exercisePreDefinitionId = exercisePreDefinition.id }

        videoRepository.saveVideoExercisePreDefinitionLocally(toVideos)
    }

    suspend fun saveWorkoutGroupPreDefinition(toWorkoutGroupPreDefinition: TOWorkoutGroupPreDefinition) {
        val workoutGroupPreDefinition = toWorkoutGroupPreDefinition.getWorkoutGroupPreDefinition()

        if (toWorkoutGroupPreDefinition.id == null) {
            workoutGroupPreDefinitionDAO.insert(workoutGroupPreDefinition)
            toWorkoutGroupPreDefinition.id = workoutGroupPreDefinition.id
        } else {
            workoutGroupPreDefinitionDAO.update(workoutGroupPreDefinition)
        }
    }
}