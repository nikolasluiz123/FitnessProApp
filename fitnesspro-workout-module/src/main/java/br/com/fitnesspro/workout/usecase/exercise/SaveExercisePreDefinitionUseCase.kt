package br.com.fitnesspro.workout.usecase.exercise

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.core.extensions.bestChronoUnit
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.extensions.toMillis
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.utils.VideoUtils
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOExercisePreDefinition
import br.com.fitnesspro.to.TOVideo
import br.com.fitnesspro.to.TOVideoExercisePreDefinition
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExercisePreDefinitionFields
import br.com.fitnesspro.workout.usecase.exercise.exception.VideoException
import br.com.fitnesspro.workout.usecase.exercise.video.SaveVideoPreDefinitionUseCase
import java.io.File
import java.time.ZoneId

class SaveExercisePreDefinitionUseCase(
    private val context: Context,
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository,
    private val personRepository: PersonRepository,
    private val saveVideoPreDefinitionUseCase: SaveVideoPreDefinitionUseCase
) {
    suspend operator fun invoke(
        toExercisePreDefinition: TOExercisePreDefinition,
        videoFiles: List<File>,
        transactional: Boolean = true
    ): List<FieldValidationError<EnumValidatedExercisePreDefinitionFields>> {
        val validations = validateExercisePreDefinition(toExercisePreDefinition)

        if (validations.isEmpty()) {
            if (transactional) {
                exercisePreDefinitionRepository.runInTransaction {
                    save(toExercisePreDefinition, videoFiles)
                }
            } else {
                save(toExercisePreDefinition, videoFiles)
            }
        }

        return validations
    }

    private suspend fun save(toExercisePreDefinition: TOExercisePreDefinition, videoFiles: List<File>) {
        if (toExercisePreDefinition.id != null) {
            exercisePreDefinitionRepository.saveExercisePreDefinitionLocally(
                toExercisePreDefinition = toExercisePreDefinition,
                toVideos = emptyList()
            )
        } else {
            val toVideos = getListTOVideoExecutionFromFiles(videoFiles)
            validateAllVideos(videoFiles, toVideos)

            toExercisePreDefinition.personalTrainerPersonId = personRepository.getAuthenticatedTOPerson()?.id!!

            exercisePreDefinitionRepository.saveExercisePreDefinitionLocally(toExercisePreDefinition, toVideos)
        }
    }

    private fun getListTOVideoExecutionFromFiles(videoFiles: List<File>): List<TOVideoExercisePreDefinition> {
        return videoFiles.map {
            val (width, height) = VideoUtils.getVideoResolution(it)!!

            TOVideoExercisePreDefinition(
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
    private suspend fun validateAllVideos(videoFiles: List<File>, toVideos: List<TOVideoExercisePreDefinition>) {
        if (toVideos.isNotEmpty()) {
            val files = videoFiles.associateBy { it.absolutePath }

            toVideos.forEach { toVideoExercisePreDefinition ->
                saveVideoPreDefinitionUseCase.validateVideo(
                    toVideoExercisePreDefinition = toVideoExercisePreDefinition,
                    videoFile = files[toVideoExercisePreDefinition.toVideo?.filePath!!]!!
                )
            }
        }
    }

    private fun validateExercisePreDefinition(toExercisePreDefinition: TOExercisePreDefinition): List<FieldValidationError<EnumValidatedExercisePreDefinitionFields>> {
        val validationResults = mutableListOf<FieldValidationError<EnumValidatedExercisePreDefinitionFields>>()
        validateExerciseName(toExercisePreDefinition)?.let(validationResults::add)
        validateExerciseRest(toExercisePreDefinition)?.let(validationResults::add)
        validateExerciseDuration(toExercisePreDefinition)?.let(validationResults::add)

        return validationResults
    }

    private fun validateExerciseName(toExercisePreDefinition: TOExercisePreDefinition): FieldValidationError<EnumValidatedExercisePreDefinitionFields>? {
        return when {
            toExercisePreDefinition.name.isNullOrEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EnumValidatedExercisePreDefinitionFields.EXERCISE.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedExercisePreDefinitionFields.EXERCISE,
                    message = message
                )
            }

            toExercisePreDefinition.name!!.length > EnumValidatedExercisePreDefinitionFields.EXERCISE.maxLength -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_field_with_max_length,
                    context.getString(EnumValidatedExercisePreDefinitionFields.EXERCISE.labelResId),
                    EnumValidatedExercisePreDefinitionFields.EXERCISE.maxLength
                )

                FieldValidationError(
                    field = EnumValidatedExercisePreDefinitionFields.EXERCISE,
                    message = message
                )
            }

            else -> null
        }
    }
    
    private fun validateExerciseRest(toExerciseExecution: TOExercisePreDefinition): FieldValidationError<EnumValidatedExercisePreDefinitionFields>? {
        val validationError = when {
            (toExerciseExecution.rest != null) != (toExerciseExecution.unitRest != null) -> {
                val message = context.getString(R.string.validation_msg_invalid_rest_or_unit)

                FieldValidationError<EnumValidatedExercisePreDefinitionFields>(
                    field = null,
                    message = message
                )
            }

            else -> null
        }

        if (validationError == null &&
            toExerciseExecution.rest != null &&
            toExerciseExecution.rest?.bestChronoUnit() != toExerciseExecution.unitRest) {

            toExerciseExecution.rest = toExerciseExecution.rest!!.toMillis(toExerciseExecution.unitRest!!)
        }

        return validationError
    }

    private fun validateExerciseDuration(toExerciseExecution: TOExercisePreDefinition): FieldValidationError<EnumValidatedExercisePreDefinitionFields>? {
        val validationError = when {
            (toExerciseExecution.duration != null) != (toExerciseExecution.unitDuration != null) -> {
                val message = context.getString(R.string.validation_msg_invalid_duration_or_unit)

                FieldValidationError<EnumValidatedExercisePreDefinitionFields>(
                    field = null,
                    message = message
                )
            }

            else -> null
        }

        if (validationError == null &&
            toExerciseExecution.duration != null &&
            toExerciseExecution.duration?.bestChronoUnit() != toExerciseExecution.unitDuration) {

            toExerciseExecution.duration = toExerciseExecution.duration!!.toMillis(toExerciseExecution.unitDuration!!)
        }

        return validationError
    }
}