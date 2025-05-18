package br.com.fitnesspro.scheduler.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.PROFESSIONAL
import br.com.fitnesspro.to.TOScheduler

class SaveCompromiseSuggestionUseCase(
    context: Context,
    schedulerRepository: SchedulerRepository,
    userRepository: UserRepository,
    private val academyRepository: AcademyRepository,
    private val personRepository: PersonRepository
): SaveCompromiseCommonUseCase(context, schedulerRepository, userRepository) {

    suspend fun saveCompromiseSuggestion(toScheduler: TOScheduler): MutableList<FieldValidationError<EnumValidatedCompromiseFields>> {
        val validationResults = validateCompromiseSuggestion(toScheduler)

        if (validationResults.isEmpty()) {
            schedulerRepository.saveScheduler(toScheduler, EnumSchedulerType.SUGGESTION)
        }

        return validationResults
    }

    private suspend fun validateCompromiseSuggestion(toScheduler: TOScheduler): MutableList<FieldValidationError<EnumValidatedCompromiseFields>> {
        val validationResults = mutableListOf(
            validateProfessional(toScheduler),
            validateSuggestionHourStart(toScheduler),
            validateSuggestionHourEnd(toScheduler),
            validateObservation(toScheduler),
            validateHourPeriod(toScheduler),
            validateSchedulerConflictProfessional(toScheduler)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private fun validateProfessional(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields>? {
        return when {
            scheduler.professionalPersonId.isNullOrEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(PROFESSIONAL.labelResId)
                )

                FieldValidationError(
                    field = PROFESSIONAL,
                    message = message
                )
            }

            else -> null
        }
    }

    private suspend fun validateSuggestionHourStart(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields>? {
        var validationResult = validateHourStart(scheduler, false)

        if (validationResult == null) {
            if (scheduler.professionalPersonId == null) return null

            val academyTimes = academyRepository.getAcademyTimes(
                personId = scheduler.professionalPersonId!!,
                dayOfWeek = scheduler.dateTimeStart?.toLocalDate()?.dayOfWeek!!
            )

            val startWorkTime = academyTimes.minOf { it.timeStart!! }
            val endWorkTime = academyTimes.maxOf { it.timeEnd!! }

            val startLocalTime = scheduler.dateTimeStart!!.toLocalTime()

            validationResult = when {
                startLocalTime < startWorkTime || startLocalTime > endWorkTime -> {
                    val message = context.getString(
                        R.string.save_compromise_start_hour_out_of_work_time_range,
                        context.getString(EnumValidatedCompromiseFields.HOUR_START.labelResId),
                        startWorkTime.format(EnumDateTimePatterns.TIME),
                        endWorkTime.format(EnumDateTimePatterns.TIME)
                    )

                    FieldValidationError(
                        field = EnumValidatedCompromiseFields.HOUR_START,
                        message = message
                    )
                }

                else -> null
            }
        }

        return validationResult
    }

    private suspend fun validateSuggestionHourEnd(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields>? {
        var validationResult = validateHourEnd(scheduler)

        if (validationResult == null) {
            if (scheduler.professionalPersonId == null) return null

            val academyTimes = academyRepository.getAcademyTimes(
                personId = scheduler.professionalPersonId!!,
                dayOfWeek = scheduler.dateTimeEnd?.dayOfWeek!!
            )

            val startWorkTime = academyTimes.minOf { it.timeStart!! }
            val endWorkTime = academyTimes.maxOf { it.timeEnd!! }
            val endLocalTime = scheduler.dateTimeEnd!!.toLocalTime()

            validationResult = when {
                endLocalTime < startWorkTime || endLocalTime > endWorkTime -> {
                    val message = context.getString(
                        R.string.save_compromise_start_hour_out_of_work_time_range,
                        context.getString(EnumValidatedCompromiseFields.HOUR_END.labelResId),
                        startWorkTime.format(EnumDateTimePatterns.TIME),
                        endWorkTime.format(EnumDateTimePatterns.TIME)
                    )

                    FieldValidationError(
                        field = EnumValidatedCompromiseFields.HOUR_END,
                        message = message
                    )
                }

                else -> null
            }
        }

        return validationResult
    }

    private suspend fun validateSchedulerConflictProfessional(toScheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields>? {
        val requiredFields = listOf(
            toScheduler.dateTimeStart,
            toScheduler.dateTimeEnd,
            toScheduler.professionalPersonId
        )

        if (requiredFields.any { it == null }) return null

        val professional = personRepository.getTOPersonById(toScheduler.professionalPersonId!!)

        val hasConflict = schedulerRepository.getHasSchedulerConflict(
            schedulerId = toScheduler.id,
            personId = toScheduler.professionalPersonId!!,
            userType = professional.user?.type!!,
            start = toScheduler.dateTimeStart!!,
            end = toScheduler.dateTimeEnd!!
        )

        return when {
            hasConflict -> {
                val message = context.getString(
                    R.string.save_compromise_scheduler_conflict,
                    toScheduler.dateTimeStart!!.format(EnumDateTimePatterns.DATE),
                    toScheduler.dateTimeStart!!.format(EnumDateTimePatterns.TIME),
                    toScheduler.dateTimeEnd!!.format(EnumDateTimePatterns.TIME),
                    professional.name
                )

                FieldValidationError(
                    field = null,
                    message = message
                )
            }

            else -> null
        }
    }
}