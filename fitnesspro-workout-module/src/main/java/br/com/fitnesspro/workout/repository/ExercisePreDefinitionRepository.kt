package br.com.fitnesspro.workout.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ExercisePreDefinitionDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupPreDefinitionDAO
import br.com.fitnesspro.mappers.getExercisePreDefinition
import br.com.fitnesspro.mappers.getTOExercisePreDefinition
import br.com.fitnesspro.mappers.getTOWorkoutGroupPreDefinition
import br.com.fitnesspro.mappers.getWorkoutGroupPreDefinition
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOExercisePreDefinition
import br.com.fitnesspro.to.TOVideoExercisePreDefinition
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition
import br.com.fitnesspro.tuple.ExercisePredefinitionGroupedTuple

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

    fun getListExercisePreDefinitions(authenticatedPersonId: String, simpleFilter: String): Pager<Int, TOExercisePreDefinition> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                exercisePreDefinitionDAO.getListPreDefinitions(
                    authenticatedPersonId = authenticatedPersonId,
                    simpleFilter = simpleFilter,
                )
            }
        )
    }

    fun getListWorkoutGroupPreDefinitions(authenticatedPersonId: String, simpleFilter: String): Pager<Int, TOWorkoutGroupPreDefinition> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                workoutGroupPreDefinitionDAO.getListPreDefinitions(
                    authenticatedPersonId = authenticatedPersonId,
                    simpleFilter = simpleFilter,
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

            toExercisePreDefinition.id = exercisePreDefinition.id
            toVideos.forEach { it.exercisePreDefinitionId = exercisePreDefinition.id }
        } else {
            exercisePreDefinitionDAO.update(exercisePreDefinition)
        }

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

    suspend fun findTOExercisePreDefinitionById(id: String): TOExercisePreDefinition? {
        return exercisePreDefinitionDAO.findExercisePreDefinitionById(id)?.getTOExercisePreDefinition()
    }

    suspend fun findTOWorkoutGroupPreDefinitionById(id: String): TOWorkoutGroupPreDefinition? {
        return workoutGroupPreDefinitionDAO.findById(id)?.getTOWorkoutGroupPreDefinition()
    }

    fun getListGroupedPredefinitions(authenticatedPersonId: String, simpleFilter: String = ""): Pager<Int, ExercisePredefinitionGroupedTuple> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                workoutGroupPreDefinitionDAO.getListGroupedPredefinitions(
                    authenticatedPersonId = authenticatedPersonId,
                    simpleFilter = simpleFilter,
                )
            }
        )
    }

    suspend fun inactivateExercisePreDefinition(exercisePreDefinitionId: String) {
        val exercisePreDefinition = exercisePreDefinitionDAO.findById(exercisePreDefinitionId).apply {
            active = false
        }

        exercisePreDefinitionDAO.update(exercisePreDefinition, true)
        videoRepository.inactivateVideoExercisePreDefinition(listOf(exercisePreDefinitionId))
    }
}