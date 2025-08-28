package br.com.fitnesspro.workout.usecase.exercise.video

import br.com.fitnesspro.workout.repository.VideoRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class InactivateVideoExecutionUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(filePaths: List<String>) = withContext(IO) {
        val ids = videoRepository.getListExerciseExecutionIdsFromVideoFilePaths(filePaths)

        videoRepository.runInTransaction {
            videoRepository.inactivateVideoExerciseExecution(ids)
        }
    }
}