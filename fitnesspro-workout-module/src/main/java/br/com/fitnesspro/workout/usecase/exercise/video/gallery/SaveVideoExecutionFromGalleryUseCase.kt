package br.com.fitnesspro.workout.usecase.exercise.video.gallery

import android.content.Context
import android.net.Uri
import br.com.fitnesspro.core.utils.VideoUtils
import br.com.fitnesspro.core.validation.ValidationError
import br.com.fitnesspro.workout.usecase.exercise.exception.VideoException
import br.com.fitnesspro.workout.usecase.exercise.video.SaveVideoExecutionUseCase

class SaveVideoExecutionFromGalleryUseCase(
    private val context: Context,
    private val saveVideoExecutionUseCase: SaveVideoExecutionUseCase
) {
    suspend operator fun invoke(exerciseExecutionId: String, uri: Uri): ValidationError? {
        return VideoUtils.createVideoFileFromUri(context, uri)?.let { videoFile ->
            saveVideoExecutionUseCase(exerciseExecutionId, videoFile)
        }
    }
}