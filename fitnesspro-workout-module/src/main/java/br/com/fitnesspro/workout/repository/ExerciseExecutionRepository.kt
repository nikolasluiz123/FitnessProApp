package br.com.fitnesspro.workout.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseExecutionDAO
import br.com.fitnesspro.mappers.getExerciseExecution
import br.com.fitnesspro.mappers.getTOExerciseExecution
import br.com.fitnesspro.mappers.getVideo
import br.com.fitnesspro.mappers.getVideoExerciseExecution
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution
import br.com.fitnesspro.to.TOExerciseExecution
import br.com.fitnesspro.to.TOVideoExerciseExecution
import br.com.fitnesspro.tuple.ExerciseExecutionGroupedTuple

class ExerciseExecutionRepository(
    context: Context,
    private val exerciseExecutionDAO: ExerciseExecutionDAO,
    private val videoRepository: VideoRepository,
    private val exerciseWebClient: ExerciseWebClient
) : FitnessProRepository(context) {

    suspend fun findById(id: String): TOExerciseExecution? {
        return exerciseExecutionDAO.findById(id)?.getTOExerciseExecution()
    }

    suspend fun newExerciseExecution(
        toExerciseExecution: TOExerciseExecution,
        toVideos: List<TOVideoExerciseExecution>
    ) {
        runInTransaction {
            saveExerciseExecutionLocally(toExerciseExecution, toVideos)
            newExerciseExecutionRemote(toExerciseExecution, toVideos)
        }
    }

    suspend fun saveExerciseExecution(toExerciseExecution: TOExerciseExecution) {
        runInTransaction {
            saveExerciseExecutionLocally(toExerciseExecution, emptyList())
            saveExerciseExecutionRemote(toExerciseExecution)
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
            exerciseExecutionDAO.update(exerciseExecution)
        }

        toExerciseExecution.id = exerciseExecution.id
        toVideos.forEach { it.exerciseExecutionId = exerciseExecution.id }

        videoRepository.saveVideoExerciseExecutionLocally(toVideos)
    }

    private suspend fun newExerciseExecutionRemote(
        toExerciseExecution: TOExerciseExecution,
        toVideos: List<TOVideoExerciseExecution>
    ) {
        val videos = mutableListOf<Video>()
        val videoExerciseExecutionList = mutableListOf<VideoExerciseExecution>()

        toVideos.forEach {
            videos.add(it.toVideo?.getVideo()!!)
            videoExerciseExecutionList.add(it.getVideoExerciseExecution())
        }

        exerciseWebClient.newExerciseExecution(
            token = getValidToken(),
            exerciseExecution = toExerciseExecution.getExerciseExecution(),
            videos = videos,
            videoExerciseExecutionList = videoExerciseExecutionList
        )
    }

    private suspend fun saveExerciseExecutionRemote(toExerciseExecution: TOExerciseExecution) {
        exerciseWebClient.saveExerciseExecution(
            token = getValidToken(),
            exerciseExecution = toExerciseExecution.getExerciseExecution(),
        )
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