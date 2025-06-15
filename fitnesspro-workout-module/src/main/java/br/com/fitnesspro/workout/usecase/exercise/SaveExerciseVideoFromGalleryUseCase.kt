package br.com.fitnesspro.workout.usecase.exercise

import android.content.Context
import android.net.Uri
import br.com.fitnesspro.core.utils.VideoUtils
import br.com.fitnesspro.core.validation.ValidationError

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