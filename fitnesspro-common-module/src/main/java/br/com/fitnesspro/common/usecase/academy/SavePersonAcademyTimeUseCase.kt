package br.com.fitnesspro.common.usecase.academy

import android.content.Context
import br.com.android.ui.compose.components.fields.validation.FieldValidationError
import br.com.core.utils.enums.EnumDateTimePatterns
import br.com.core.utils.extensions.format
import br.com.core.utils.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.to.TOPersonAcademyTime
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class SavePersonAcademyTimeUseCase(
    private val context: Context,
    private val academyRepository: AcademyRepository
) {

    suspend fun execute(toPersonAcademyTime: TOPersonAcademyTime): List<FieldValidationError<EnumValidatedAcademyFields>> = withContext(IO) {
        val validationsResults = getAllValidationResults(toPersonAcademyTime)

        if (validationsResults.isEmpty()) {
            academyRepository.savePersonAcademyTime(toPersonAcademyTime)
        }

        validationsResults
    }

    private suspend fun getAllValidationResults(toPersonAcademyTime: TOPersonAcademyTime): List<FieldValidationError<EnumValidatedAcademyFields>> {
        return mutableListOf(
            validateAcademy(toPersonAcademyTime),
            validateStart(toPersonAcademyTime),
            validateEnd(toPersonAcademyTime),
            validateTimePeriod(toPersonAcademyTime),
            validateDayOfWeek(toPersonAcademyTime),
            validateRepeat(toPersonAcademyTime)
        ).filterNotNull()
    }

    private fun validateAcademy(toPersonAcademyTime: TOPersonAcademyTime): FieldValidationError<EnumValidatedAcademyFields>? {
        val validationPair = when {
            toPersonAcademyTime.academyId.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedAcademyFields.ACADEMY.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedAcademyFields.ACADEMY,
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateStart(toPersonAcademyTime: TOPersonAcademyTime): FieldValidationError<EnumValidatedAcademyFields>? {
        val validationPair = when {
            toPersonAcademyTime.timeStart == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedAcademyFields.TIME_START.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedAcademyFields.TIME_START,
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateEnd(toPersonAcademyTime: TOPersonAcademyTime): FieldValidationError<EnumValidatedAcademyFields>? {
        val validationPair = when {
            toPersonAcademyTime.timeEnd == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedAcademyFields.TIME_END.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedAcademyFields.TIME_END,
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateTimePeriod(toPersonAcademyTime: TOPersonAcademyTime): FieldValidationError<EnumValidatedAcademyFields>? {
        if (toPersonAcademyTime.timeStart == null || toPersonAcademyTime.timeEnd == null) return null

        return when {
            toPersonAcademyTime.timeStart!!.isAfter(toPersonAcademyTime.timeEnd) ||
            toPersonAcademyTime.timeStart == toPersonAcademyTime.timeEnd -> {
                val message = context.getString(R.string.save_person_academy_time_msg_invalid_time_period)

                FieldValidationError(
                    field = null,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun validateDayOfWeek(toPersonAcademyTime: TOPersonAcademyTime): FieldValidationError<EnumValidatedAcademyFields>? {
        val validationPair = when {
            toPersonAcademyTime.dayOfWeek == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedAcademyFields.DAY_OF_WEEK.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedAcademyFields.DAY_OF_WEEK,
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private suspend fun validateRepeat(toPersonAcademyTime: TOPersonAcademyTime): FieldValidationError<EnumValidatedAcademyFields>? {
        val requiredFields = listOf(
            toPersonAcademyTime.dayOfWeek,
            toPersonAcademyTime.timeStart,
            toPersonAcademyTime.timeEnd
        )

        if (requiredFields.any { it == null }) return null

        val conflict = academyRepository.getConflictPersonAcademyTime(toPersonAcademyTime)

        return if (conflict != null) {
            val message = context.getString(
                R.string.validation_msg_person_academy_time_conflict,
                conflict.dayOfWeek?.getFirstPartFullDisplayName(),
                conflict.timeStart?.format(EnumDateTimePatterns.TIME),
                conflict.timeEnd?.format(EnumDateTimePatterns.TIME)
            )

            FieldValidationError(
                field = null,
                message = message
            )
        } else {
            null
        }
    }
}