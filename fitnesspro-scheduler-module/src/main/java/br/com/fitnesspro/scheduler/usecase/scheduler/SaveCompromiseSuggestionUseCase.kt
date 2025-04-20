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
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.RECURRENT_SCHEDULER_CONFLICT
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.REQUIRED_PROFESSIONAL
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

    suspend fun saveCompromiseSuggestion(toScheduler: TOScheduler): MutableList<FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>> {
        val validationResults = validateCompromiseSuggestion(toScheduler)

        if (validationResults.isEmpty()) {
            schedulerRepository.saveScheduler(toScheduler, EnumSchedulerType.SUGGESTION)
        }

        return validationResults
    }

    private suspend fun validateCompromiseSuggestion(toScheduler: TOScheduler): MutableList<FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>> {
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

    private fun validateProfessional(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        return when {
            scheduler.professionalPersonId.isNullOrEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(PROFESSIONAL.labelResId)
                )

                FieldValidationError(
                    field = PROFESSIONAL,
                    validationType = REQUIRED_PROFESSIONAL,
                    message = message
                )
            }

            else -> null
        }
    }

    private suspend fun validateSuggestionHourStart(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        var validationResult = validateHourStart(scheduler)

        if (validationResult == null) {
            if (scheduler.professionalPersonId == null) return null

            val academyTimes = academyRepository.getAcademyTimes(
                personId = scheduler.professionalPersonId!!,
                dayOfWeek = scheduler.scheduledDate!!.dayOfWeek
            )

            val startWorkTime = academyTimes.minOf { it.timeStart!! }
            val endWorkTime = academyTimes.maxOf { it.timeEnd!! }

            validationResult = when {
                scheduler.timeStart!! < startWorkTime || scheduler.timeStart!! > endWorkTime -> {
                    val message = context.getString(
                        R.string.save_compromise_start_hour_out_of_work_time_range,
                        context.getString(EnumValidatedCompromiseFields.HOUR_START.labelResId),
                        startWorkTime.format(EnumDateTimePatterns.TIME),
                        endWorkTime.format(EnumDateTimePatterns.TIME)
                    )

                    FieldValidationError(
                        field = EnumValidatedCompromiseFields.HOUR_START,
                        validationType = EnumCompromiseValidationTypes.START_HOUR_OUT_OF_WORK_TIME_RANGE,
                        message = message
                    )
                }

                else -> null
            }
        }

        return validationResult
    }

    private suspend fun validateSuggestionHourEnd(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        var validationResult = validateHourEnd(scheduler)

        if (validationResult == null) {
            if (scheduler.professionalPersonId == null) return null

            val academyTimes = academyRepository.getAcademyTimes(
                personId = scheduler.professionalPersonId!!,
                dayOfWeek = scheduler.scheduledDate!!.dayOfWeek
            )

            val startWorkTime = academyTimes.minOf { it.timeStart!! }
            val endWorkTime = academyTimes.maxOf { it.timeEnd!! }

            validationResult = when {
                scheduler.timeEnd!! < startWorkTime || scheduler.timeEnd!! > endWorkTime -> {
                    val message = context.getString(
                        R.string.save_compromise_start_hour_out_of_work_time_range,
                        context.getString(EnumValidatedCompromiseFields.HOUR_END.labelResId),
                        startWorkTime.format(EnumDateTimePatterns.TIME),
                        endWorkTime.format(EnumDateTimePatterns.TIME)
                    )

                    FieldValidationError(
                        field = EnumValidatedCompromiseFields.HOUR_END,
                        validationType = EnumCompromiseValidationTypes.END_HOUR_OUT_OF_WORK_TIME_RANGE,
                        message = message
                    )
                }

                else -> null
            }
        }

        return validationResult
    }

    private suspend fun validateSchedulerConflictProfessional(toScheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        val requiredFields = listOf(
            toScheduler.timeStart,
            toScheduler.timeEnd,
            toScheduler.professionalPersonId
        )

        if (requiredFields.any { it == null }) return null

        val professional = personRepository.getTOPersonById(toScheduler.professionalPersonId!!)

        val hasConflict = schedulerRepository.getHasSchedulerConflict(
            schedulerId = toScheduler.id,
            personId = toScheduler.professionalPersonId!!,
            userType = professional.user?.type!!,
            scheduledDate = toScheduler.scheduledDate!!,
            start = toScheduler.timeStart!!,
            end = toScheduler.timeEnd!!
        )

        return when {
            hasConflict -> {
                val message = context.getString(
                    R.string.save_compromise_scheduler_conflict,
                    toScheduler.scheduledDate!!.format(EnumDateTimePatterns.DATE),
                    toScheduler.timeStart!!.format(EnumDateTimePatterns.TIME),
                    toScheduler.timeEnd!!.format(EnumDateTimePatterns.TIME),
                    professional.name
                )

                FieldValidationError(
                    field = null,
                    validationType = RECURRENT_SCHEDULER_CONFLICT,
                    message = message
                )
            }

            else -> null
        }
    }
}