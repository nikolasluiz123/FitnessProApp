package br.com.fitnesspro.workout.usecase.exercise.video.gallery

import android.content.Context
import android.net.Uri
import br.com.android.ui.compose.components.fields.validation.ValidationError
import br.com.core.android.utils.media.VideoUtils
import br.com.fitnesspro.workout.usecase.exercise.video.SaveVideoPreDefinitionUseCase

class SaveVideoExercisePreDefinitionFromGalleryUseCase(
    private val context: Context,
    private val saveVideoPreDefinitionUseCase: SaveVideoPreDefinitionUseCase
) {
    suspend operator fun invoke(exercisePreDefinitionId: String, uri: Uri): ValidationError? {
        return VideoUtils.createVideoFileFromUri(context, uri)?.let { videoFile ->
            saveVideoPreDefinitionUseCase(exercisePreDefinitionId, videoFile)
        }
    }
}