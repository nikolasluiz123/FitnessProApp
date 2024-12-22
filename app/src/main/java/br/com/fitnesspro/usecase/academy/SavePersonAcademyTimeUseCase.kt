package br.com.fitnesspro.usecase.academy

import android.content.Context
import br.com.fitnesspro.R
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.repository.AcademyRepository
import br.com.fitnesspro.to.TOPersonAcademyTime

class SavePersonAcademyTimeUseCase(
    private val context: Context,
    private val academyRepository: AcademyRepository,
) {

    suspend fun execute(toPersonAcademyTime: TOPersonAcademyTime): List<Pair<EnumValidatedAcademyFields?, String>> {
        val personAcademyTime = PersonAcademyTime(
            personId = toPersonAcademyTime.personId!!,
            academyId = toPersonAcademyTime.toAcademy?.id!!,
            timeStart = toPersonAcademyTime.timeStart,
            timeEnd = toPersonAcademyTime.timeEnd,
            dayOfWeek = toPersonAcademyTime.dayOfWeek,
        )

        val validationsResults = mutableListOf(
            validateAcademy(personAcademyTime),
            validateStart(personAcademyTime),
            validateEnd(personAcademyTime),
            validateDayOfWeek(personAcademyTime),
            validateRepeat(personAcademyTime)
        ).filterNotNull()

        if (validationsResults.isEmpty()) {
            academyRepository.savePersonAcademyTime(personAcademyTime)
        }

        return validationsResults
    }

    private fun validateAcademy(personAcademyTime: PersonAcademyTime): Pair<EnumValidatedAcademyFields, String>? {
        val validationPair = when {
            personAcademyTime.academyId.isNullOrEmpty() -> {
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

    private fun validateStart(personAcademyTime: PersonAcademyTime): Pair<EnumValidatedAcademyFields, String>? {
        val validationPair = when {
            personAcademyTime.timeStart == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedAcademyFields.DATE_TIME_START.labelResId)
                )

                Pair(EnumValidatedAcademyFields.DATE_TIME_START, message)
            }

            personAcademyTime.timeStart!! > personAcademyTime.timeEnd -> {
                val message = context.getString(
                    R.string.validation_msg_invalid_field,
                    context.getString(EnumValidatedAcademyFields.DATE_TIME_START.labelResId)
                )

                Pair(EnumValidatedAcademyFields.DATE_TIME_START, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateEnd(personAcademyTime: PersonAcademyTime): Pair<EnumValidatedAcademyFields, String>? {
        val validationPair = when {
            personAcademyTime.timeEnd == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedAcademyFields.DATE_TIME_END.labelResId)
                )

                Pair(EnumValidatedAcademyFields.DATE_TIME_END, message)
            }

            personAcademyTime.timeEnd!! < personAcademyTime.timeStart -> {
                val message = context.getString(
                    R.string.validation_msg_invalid_field,
                    context.getString(EnumValidatedAcademyFields.DATE_TIME_END.labelResId)
                )

                Pair(EnumValidatedAcademyFields.DATE_TIME_END, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateDayOfWeek(personAcademyTime: PersonAcademyTime): Pair<EnumValidatedAcademyFields, String>? {
        val validationPair = when {
            personAcademyTime.dayOfWeek == null -> {
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

    private suspend fun validateRepeat(personAcademyTime: PersonAcademyTime): Pair<EnumValidatedAcademyFields?, String>? {
        val conflict = academyRepository.getConflictPersonAcademyTime(personAcademyTime)

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