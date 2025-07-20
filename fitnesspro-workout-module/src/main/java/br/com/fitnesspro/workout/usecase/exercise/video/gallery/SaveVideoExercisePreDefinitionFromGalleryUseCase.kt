package br.com.fitnesspro.workout.usecase.exercise.video.gallery

import android.content.Context
import android.net.Uri
import br.com.fitnesspro.core.utils.VideoUtils
import br.com.fitnesspro.core.validation.ValidationError
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