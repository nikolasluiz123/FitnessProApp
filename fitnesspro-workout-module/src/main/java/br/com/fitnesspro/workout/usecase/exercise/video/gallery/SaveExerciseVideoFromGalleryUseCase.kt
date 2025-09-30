package br.com.fitnesspro.workout.usecase.exercise.video.gallery

import android.content.Context
import android.net.Uri
import br.com.android.ui.compose.components.fields.validation.ValidationError
import br.com.core.android.utils.media.VideoUtils
import br.com.fitnesspro.workout.usecase.exercise.video.SaveExerciseVideoUseCase

class SaveExerciseVideoFromGalleryUseCase(
    private val context: Context,
    private val saveExerciseVideoUseCase: SaveExerciseVideoUseCase,
) {

    suspend operator fun invoke(exerciseId: String, uri: Uri): ValidationError? {
        return VideoUtils.createVideoFileFromUri(context, uri)?.let { videoFile ->
            saveExerciseVideoUseCase(exerciseId, videoFile)
        }
    }
}