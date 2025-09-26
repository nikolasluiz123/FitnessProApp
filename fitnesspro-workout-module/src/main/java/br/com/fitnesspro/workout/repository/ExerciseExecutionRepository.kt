package br.com.fitnesspro.workout.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseExecutionDAO
import br.com.fitnesspro.mappers.getExerciseExecution
import br.com.fitnesspro.mappers.getTOExerciseExecution
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import br.com.fitnesspro.to.TOExerciseExecution
import br.com.fitnesspro.to.TOVideoExerciseExecution
import br.com.fitnesspro.tuple.ExecutionDurationTuple
import br.com.fitnesspro.tuple.ExecutionEvolutionHistoryGroupedTuple
import br.com.fitnesspro.tuple.ExerciseExecutionGroupedTuple
import br.com.fitnesspro.tuple.charts.ExerciseExecutionChartTuple
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class ExerciseExecutionRepository(
    context: Context,
    private val exerciseExecutionDAO: ExerciseExecutionDAO,
    private val videoRepository: VideoRepository,
    private val personRepository: PersonRepository
) : FitnessProRepository(context) {

    suspend fun findById(id: String): TOExerciseExecution? = withContext(IO) {
        exerciseExecutionDAO.findById(id)?.getTOExerciseExecution()
    }

    suspend fun saveExerciseExecution(
        toExerciseExecution: TOExerciseExecution,
        toVideos: List<TOVideoExerciseExecution> = emptyList()
    ) {
        runInTransaction {
            saveExerciseExecutionLocally(toExerciseExecution, toVideos)
        }
    }

    private suspend fun saveExerciseExecutionLocally(
        toExerciseExecution: TOExerciseExecution,
        toVideos: List<TOVideoExerciseExecution>
    ) {
        val exerciseExecution = toExerciseExecution.getExerciseExecution()

        if (toExerciseExecution.id == null) {
            exerciseExecutionDAO.insert(exerciseExecution)
        } else {
            exerciseExecutionDAO.update(exerciseExecution, true)
        }

        toExerciseExecution.id = exerciseExecution.id
        toVideos.forEach { it.exerciseExecutionId = exerciseExecution.id }

        videoRepository.saveVideoExerciseExecutionLocally(toVideos)
    }

    suspend fun getActualExecutionSet(exerciseId: String): Int {
        return exerciseExecutionDAO.getActualExecutionSet(exerciseId)
    }

    fun getListExerciseExecutionGrouped(exerciseId: String): Pager<Int, ExerciseExecutionGroupedTuple> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                exerciseExecutionDAO.getListExerciseExecutionGrouped(
                    exerciseId = exerciseId
                )
            }
        )
    }

    suspend fun getListExecutionHistoryGrouped(personMemberId: String, simpleFilter: String = ""): Pager<Int, ExecutionEvolutionHistoryGroupedTuple> {
        val personId = personRepository.getAuthenticatedTOPerson()?.id!!

        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                exerciseExecutionDAO.getListExecutionHistoryGrouped(
                    authenticatedPersonId = personId,
                    simpleFilter = simpleFilter,
                    personMemberId = personMemberId
                )
            }
        )
    }

    suspend fun inactivateExerciseExecution(exerciseExecutionId: String) {
        val exerciseExecution = exerciseExecutionDAO.findById(exerciseExecutionId)!!.apply {
            active = false
        }

        exerciseExecutionDAO.update(exerciseExecution, true)
        videoRepository.inactivateVideoExerciseExecution(listOf(exerciseExecutionId))

        reorderExecutions(exerciseExecution)
    }

    private suspend fun reorderExecutions(exerciseExecution: ExerciseExecution) {
        val exercisesReorder = exerciseExecutionDAO.getListActiveExerciseExecution(
            exerciseId = exerciseExecution.exerciseId!!,
            date = exerciseExecution.executionStartTime.toString()
        )

        if (exercisesReorder.isNotEmpty()) {
            exercisesReorder.forEachIndexed { index, exerciseExecution ->
                exerciseExecution.actualSet = index + 1
            }

            exerciseExecutionDAO.updateBatch(exercisesReorder, true)
        }
    }

    suspend fun getListExerciseExecutionGroupedBarChartTuple(exerciseId: String): List<ExerciseExecutionChartTuple> {
        return exerciseExecutionDAO.getListExerciseExecutionGroupedBarChartTuple(exerciseId)
    }
    suspend fun getExecutionStartEnd(workoutId: String): ExecutionDurationTuple? = withContext(IO) {
        exerciseExecutionDAO.getExecutionStartEnd(workoutId)
    }
}