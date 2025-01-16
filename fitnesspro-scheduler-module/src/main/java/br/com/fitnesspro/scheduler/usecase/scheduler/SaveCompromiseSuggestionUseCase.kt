package br.com.fitnesspro.scheduler.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.validation.ValidationError
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.END_HOUR_OUT_OF_WORK_TIME_RANGE
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.RECURRENT_SCHEDULER_CONFLICT
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.REQUIRED_PROFESSIONAL
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.START_HOUR_OUT_OF_WORK_TIME_RANGE
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.HOUR_END
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.HOUR_START
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.PROFESSIONAL
import br.com.fitnesspro.to.TOScheduler
import java.time.LocalTime

class SaveCompromiseSuggestionUseCase(
    context: Context,
    schedulerRepository: SchedulerRepository,
    userRepository: UserRepository,
    private val schedulerConfigRepository: SchedulerConfigRepository
): SaveCompromiseCommonUseCase(context, schedulerRepository, userRepository) {

    suspend fun saveCompromiseSuggestion(toScheduler: TOScheduler): MutableList<ValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>> {
        val validationResults = validateCompromiseSuggestion(toScheduler)

        if (validationResults.isEmpty()) {
            schedulerRepository.saveScheduler(toScheduler)
        }

        return validationResults
    }

    private suspend fun validateCompromiseSuggestion(toScheduler: TOScheduler): MutableList<ValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>> {
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

    private fun validateProfessional(scheduler: TOScheduler): ValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        return when {
            scheduler.professionalPersonId.isNullOrEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(PROFESSIONAL.labelResId)
                )

                ValidationError(
                    field = PROFESSIONAL,
                    validationType = REQUIRED_PROFESSIONAL,
                    message = message
                )
            }

            else -> null
        }
    }

    private suspend fun validateSuggestionHourStart(scheduler: TOScheduler): ValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        var validationResult = validateHourStart(scheduler)

        if (validationResult == null) {
            val config = schedulerConfigRepository.getTOSchedulerConfigByPersonId(scheduler.professionalPersonId!!)!!
            val startWorkTime = config.startWorkTime!!
            val endWorkTime = config.endWorkTime!!

            validationResult = when {
                scheduler.start!! < startWorkTime || scheduler.start!! > endWorkTime -> {
                    val message = context.getString(
                        R.string.save_compromise_start_hour_out_of_work_time_range,
                        context.getString(HOUR_START.labelResId),
                        startWorkTime.format(EnumDateTimePatterns.TIME),
                        endWorkTime.format(EnumDateTimePatterns.TIME)
                    )

                    ValidationError(
                        field = HOUR_START,
                        validationType = START_HOUR_OUT_OF_WORK_TIME_RANGE,
                        message = message
                    )
                }

                else -> null
            }
        }

        return validationResult
    }

    private suspend fun validateSuggestionHourEnd(scheduler: TOScheduler): ValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        var validationResult = validateHourEnd(scheduler)

        if (validationResult == null) {
            val config = schedulerConfigRepository.getTOSchedulerConfigByPersonId(scheduler.professionalPersonId!!)!!
            val startWorkTime = config.startWorkTime!!
            val endWorkTime = config.endWorkTime!!

            validationResult = when {
                scheduler.end!! < startWorkTime || scheduler.end!! > endWorkTime -> {
                    val message = context.getString(
                        R.string.save_compromise_start_hour_out_of_work_time_range,
                        context.getString(HOUR_END.labelResId),
                        startWorkTime.format(EnumDateTimePatterns.TIME),
                        endWorkTime.format(EnumDateTimePatterns.TIME)
                    )

                    ValidationError(
                        field = HOUR_END,
                        validationType = END_HOUR_OUT_OF_WORK_TIME_RANGE,
                        message = message
                    )
                }

                else -> null
            }
        }

        return validationResult
    }

    private suspend fun validateSchedulerConflictProfessional(toScheduler: TOScheduler): ValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        if (toScheduler.start == null || toScheduler.end == null) return null

        val professional = userRepository.getTOPersonById(toScheduler.professionalPersonId!!)

        val hasConflict = schedulerRepository.getHasSchedulerConflict(
            schedulerId = toScheduler.id,
            personId = toScheduler.professionalPersonId!!,
            userType = professional.toUser?.type!!,
            scheduledDate = toScheduler.scheduledDate!!,
            start = toScheduler.start!!,
            end = toScheduler.end!!
        )

        return when {
            hasConflict -> {
                val schedulerConfig = schedulerConfigRepository.getTOSchedulerConfigByPersonId(professional.id!!)!!
                val schedulers = schedulerRepository.getSchedulerList(
                    scheduledDate = toScheduler.scheduledDate,
                    toPerson = professional
                )
                val availableSlots = getProfessionalAvailableSlots(
                    workHours = TimeSlot(
                        schedulerConfig.startWorkTime!!,
                        schedulerConfig.endWorkTime!!
                    ),
                    lunchBreak = TimeSlot(
                        schedulerConfig.startBreakTime!!,
                        schedulerConfig.endBreakTime!!
                    ),
                    appointments = schedulers.map { TimeSlot(it.start!!, it.end!!) }
                ).joinToString(separator = "\n") { it.format(context) }

                val message = if (availableSlots.isEmpty()) {
                    context.getString(
                        R.string.save_compromise_scheduler_conflict,
                        professional.name!!,
                        toScheduler.scheduledDate!!.format(EnumDateTimePatterns.DATE)
                    )
                } else {
                    context.getString(
                        R.string.save_compromise_scheduler_conflict_with_suggestions,
                        professional.name!!,
                        toScheduler.scheduledDate!!.format(EnumDateTimePatterns.DATE),
                        availableSlots
                    )
                }

                ValidationError(
                    field = null,
                    validationType = RECURRENT_SCHEDULER_CONFLICT,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun getProfessionalAvailableSlots(
        workHours: TimeSlot,
        lunchBreak: TimeSlot,
        appointments: List<TimeSlot>
    ): List<TimeSlot> {
        val allOccupiedSlots = appointments + lunchBreak
        val sortedOccupiedSlots = allOccupiedSlots.sortedBy { it.start }
        val availableSlots = mutableListOf<TimeSlot>()
        var currentStart = workHours.start

        for (slot in sortedOccupiedSlots) {
            if (currentStart < slot.start) {
                availableSlots.add(TimeSlot(currentStart, slot.start))
            }
            currentStart = maxOf(currentStart, slot.end)
        }

        if (currentStart < workHours.end) {
            availableSlots.add(TimeSlot(currentStart, workHours.end))
        }

        return availableSlots
    }

    private data class TimeSlot(val start: LocalTime, val end: LocalTime) {
        fun format(context: Context): String {
            return context.getString(
                R.string.save_compromise_time_slot,
                start.format(EnumDateTimePatterns.TIME),
                end.format(EnumDateTimePatterns.TIME)
            )
        }
    }
}