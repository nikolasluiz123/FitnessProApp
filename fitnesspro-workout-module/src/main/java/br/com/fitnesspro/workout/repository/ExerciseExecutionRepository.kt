package br.com.fitnesspro.workout.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseExecutionDAO
import br.com.fitnesspro.mappers.getExerciseExecution
import br.com.fitnesspro.mappers.getTOExerciseExecution
import br.com.fitnesspro.to.TOExerciseExecution
import br.com.fitnesspro.to.TOVideoExerciseExecution
import br.com.fitnesspro.tuple.ExerciseExecutionGroupedTuple

class ExerciseExecutionRepository(
    context: Context,
    private val exerciseExecutionDAO: ExerciseExecutionDAO,
    private val videoRepository: VideoRepository,
) : FitnessProRepository(context) {

    suspend fun findById(id: String): TOExerciseExecution? {
        return exerciseExecutionDAO.findById(id)?.getTOExerciseExecution()
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
}