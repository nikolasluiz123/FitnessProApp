package br.com.fitnesspro.common.usecase.academy

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.to.TOPersonAcademyTime

class SavePersonAcademyTimeUseCase(
    private val context: Context,
    private val academyRepository: AcademyRepository
) {

    suspend fun execute(toPersonAcademyTime: TOPersonAcademyTime): List<Pair<EnumValidatedAcademyFields?, String>> {
        val validationsResults = mutableListOf(
            validateAcademy(toPersonAcademyTime),
            validateStart(toPersonAcademyTime),
            validateEnd(toPersonAcademyTime),
            validateTimePeriod(toPersonAcademyTime),
            validateDayOfWeek(toPersonAcademyTime),
            validateRepeat(toPersonAcademyTime)
        ).filterNotNull()

        if (validationsResults.isEmpty()) {
            academyRepository.savePersonAcademyTime(toPersonAcademyTime)
        }

        return validationsResults
    }

    private fun validateAcademy(toPersonAcademyTime: TOPersonAcademyTime): Pair<EnumValidatedAcademyFields, String>? {
        val validationPair = when {
            toPersonAcademyTime.toAcademy?.id.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedAcademyFields.ACADEMY.labelResId)
                )

                Pair(EnumValidatedAcademyFields.ACADEMY, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateStart(toPersonAcademyTime: TOPersonAcademyTime): Pair<EnumValidatedAcademyFields, String>? {
        val validationPair = when {
            toPersonAcademyTime.timeStart == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedAcademyFields.DATE_TIME_START.labelResId)
                )

                Pair(EnumValidatedAcademyFields.DATE_TIME_START, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateEnd(toPersonAcademyTime: TOPersonAcademyTime): Pair<EnumValidatedAcademyFields, String>? {
        val validationPair = when {
            toPersonAcademyTime.timeEnd == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedAcademyFields.DATE_TIME_END.labelResId)
                )

                Pair(EnumValidatedAcademyFields.DATE_TIME_END, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateTimePeriod(toPersonAcademyTime: TOPersonAcademyTime): Pair<EnumValidatedAcademyFields?, String>? {
        if (toPersonAcademyTime.timeStart == null || toPersonAcademyTime.timeEnd == null) return null

        val validationPair = when {
            toPersonAcademyTime.timeStart!!.isAfter(toPersonAcademyTime.timeEnd) ||
            toPersonAcademyTime.timeStart == toPersonAcademyTime.timeEnd -> {
                val message = context.getString(R.string.save_person_academy_time_msg_invalid_time_period)

                return Pair(null, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateDayOfWeek(toPersonAcademyTime: TOPersonAcademyTime): Pair<EnumValidatedAcademyFields, String>? {
        val validationPair = when {
            toPersonAcademyTime.dayOfWeek == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedAcademyFields.DAY_OF_WEEK.labelResId)
                )

                Pair(EnumValidatedAcademyFields.DAY_OF_WEEK, message)
            }

            else -> null
        }

        return validationPair
    }

    private suspend fun validateRepeat(toPersonAcademyTime: TOPersonAcademyTime): Pair<EnumValidatedAcademyFields?, String>? {
        val conflict = academyRepository.getConflictPersonAcademyTime(toPersonAcademyTime)

        return if (conflict != null) {
            val message = context.getString(
                R.string.validation_msg_person_academy_time_conflict,
                conflict.dayOfWeek?.getFirstPartFullDisplayName(),
                conflict.timeStart?.format(EnumDateTimePatterns.TIME),
                conflict.timeEnd?.format(EnumDateTimePatterns.TIME)
            )

            Pair(null, message)
        } else {
            null
        }
    }
}