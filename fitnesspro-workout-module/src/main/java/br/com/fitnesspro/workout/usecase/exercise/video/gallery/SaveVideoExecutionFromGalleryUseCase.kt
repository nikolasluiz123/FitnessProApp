package br.com.fitnesspro.workout.usecase.exercise.video.gallery

import android.content.Context
import android.net.Uri
import br.com.android.ui.compose.components.fields.validation.ValidationError
import br.com.core.android.utils.media.VideoUtils
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