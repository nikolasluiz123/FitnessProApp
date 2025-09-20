package br.com.fitnesspro.workout.usecase.exercise

import android.content.Context
import br.com.fitnesspro.core.extensions.bestChronoUnit
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.extensions.toMillis
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.utils.VideoUtils
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOExerciseExecution
import br.com.fitnesspro.to.TOVideo
import br.com.fitnesspro.to.TOVideoExerciseExecution
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.ExerciseExecutionRepository
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExerciseExecutionFields
import br.com.fitnesspro.workout.usecase.exercise.exception.VideoException
import br.com.fitnesspro.workout.usecase.exercise.video.SaveVideoExecutionUseCase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.time.ZoneId

class SaveExerciseExecutionUseCase(
    private val context: Context,
    private val exerciseExecutionRepository: ExerciseExecutionRepository,
    private val saveVideoExecutionUseCase: SaveVideoExecutionUseCase
) {
    suspend operator fun invoke(
        toExerciseExecution: TOExerciseExecution,
        videoFiles: List<File>
    ): List<FieldValidationError<EnumValidatedExerciseExecutionFields>> = withContext(IO) {
        val validations = validateExerciseExecution(toExerciseExecution)

        if (validations.isEmpty()) {
            if (toExerciseExecution.id != null) {
                exerciseExecutionRepository.saveExerciseExecution(toExerciseExecution)
            } else {
                toExerciseExecution.set = exerciseExecutionRepository.getActualExecutionSet(toExerciseExecution.exerciseId!!)

                val toVideos = getListTOVideoExecutionFromFiles(videoFiles)

                validateAllVideos(videoFiles, toVideos)
                exerciseExecutionRepository.saveExerciseExecution(toExerciseExecution, toVideos)
            }
        }

        validations
    }

    private fun getListTOVideoExecutionFromFiles(videoFiles: List<File>): List<TOVideoExerciseExecution> {
        return videoFiles.map {
            val (width, height) = VideoUtils.getVideoResolution(it)!!

            TOVideoExerciseExecution(
                toVideo = TOVideo(
                    filePath = it.absolutePath,
                    extension = it.extension,
                    date = dateTimeNow(ZoneId.systemDefault()),
                    kbSize = FileUtils.getFileSizeInKB(it),
                    seconds = VideoUtils.getVideoDurationInSeconds(it),
                    width = width,
                    height = height
                )
            )
        }
    }

    @Throws(VideoException::class)
    private suspend fun validateAllVideos(videoFiles: List<File>, toVideos: List<TOVideoExerciseExecution>) {
        if (toVideos.isNotEmpty()) {
            val files = videoFiles.associateBy { it.absolutePath }

            toVideos.forEach { toVideoExecution ->
                saveVideoExecutionUseCase.validateVideo(
                    toVideoExerciseExecution = toVideoExecution,
                    videoFile = files[toVideoExecution.toVideo?.filePath!!]!!
                )
            }
        }
    }

    private fun validateExerciseExecution(toExerciseExecution: TOExerciseExecution): List<FieldValidationError<EnumValidatedExerciseExecutionFields>> {
        val validationResults = mutableListOf<FieldValidationError<EnumValidatedExerciseExecutionFields>>()
        validateExerciseRest(toExerciseExecution)?.let(validationResults::add)
        validateExerciseDuration(toExerciseExecution)?.let(validationResults::add)

        return validationResults
    }

    private fun validateExerciseRest(toExerciseExecution: TOExerciseExecution): FieldValidationError<EnumValidatedExerciseExecutionFields>? {
        val validationError = when {
            (toExerciseExecution.rest != null) != (toExerciseExecution.restUnit != null) -> {
                val message = context.getString(R.string.validation_msg_invalid_rest_or_unit)

                FieldValidationError<EnumValidatedExerciseExecutionFields>(
                    field = null,
                    message = message
                )
            }

            else -> null
        }

        if (validationError == null &&
            toExerciseExecution.rest != null &&
            toExerciseExecution.rest?.bestChronoUnit() != toExerciseExecution.restUnit) {

            toExerciseExecution.rest = toExerciseExecution.rest!!.toMillis(toExerciseExecution.restUnit!!)
        }

        return validationError
    }

    private fun validateExerciseDuration(toExerciseExecution: TOExerciseExecution): FieldValidationError<EnumValidatedExerciseExecutionFields>? {
        val validationError = when {
            (toExerciseExecution.duration != null) != (toExerciseExecution.durationUnit != null) -> {
                val message = context.getString(R.string.validation_msg_invalid_duration_or_unit)

                FieldValidationError<EnumValidatedExerciseExecutionFields>(
                    field = null,
                    message = message
                )
            }

            else -> null
        }

        if (validationError == null &&
            toExerciseExecution.duration != null &&
            toExerciseExecution.duration?.bestChronoUnit() != toExerciseExecution.durationUnit) {

            toExerciseExecution.duration = toExerciseExecution.duration!!.toMillis(toExerciseExecution.durationUnit!!)
        }

        return validationError
    }
}