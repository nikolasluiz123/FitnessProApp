package br.com.fitnesspro.workout.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ExercisePreDefinitionDAO
import br.com.fitnesspro.mappers.getTOExercisePreDefinition
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOExercisePreDefinition

class ExercisePreDefinitionRepository(
    context: Context,
    private val exercisePreDefinitionDAO: ExercisePreDefinitionDAO
): FitnessProRepository(context) {

    fun getExercisesPreDefinitionFromWorkoutGroup(
        workoutGroupName: String,
        authenticatedPersonId: String,
        simpleFilter: String
    ): Pager<Int, TOExercise> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                exercisePreDefinitionDAO.getExercisesPreDefinitionFromWorkoutGroup(
                    workoutGroupName = workoutGroupName,
                    authenticatedPersonId = authenticatedPersonId,
                    simpleFilter = simpleFilter
                )
            }
        )
    }

    suspend fun findExercisePreDefinitionByName(name: String): TOExercisePreDefinition? {
        return exercisePreDefinitionDAO.findExercisePreDefinitionByName(name)?.getTOExercisePreDefinition()
    }
}