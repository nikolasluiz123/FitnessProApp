package br.com.fitnesspro.scheduler.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields
import br.com.fitnesspro.to.TOScheduler
import java.time.DayOfWeek
import java.time.LocalDate

class SaveRecurrentCompromiseUseCase(
    context: Context,
    schedulerRepository: SchedulerRepository,
    userRepository: UserRepository,
    private val personRepository: PersonRepository
): SaveCompromiseCommonUseCase(context, schedulerRepository, userRepository) {

    suspend fun saveRecurrentCompromise(toScheduler: TOScheduler, config: CompromiseRecurrentConfig): MutableList<FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>> {
        val validations = mutableListOf<FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>>()
        validations.addAll(validateCommonInfosRecurrentCompromise(toScheduler))
        validations.addAll(validateConfigRecurrentCompromise(toScheduler, config))

        if (validations.isEmpty()) {
            val scheduleDates = generateSequence(config.dateStart!!) { it.plusDays(1) }
                .takeWhile { it <= config.dateEnd!! }
                .filter { config.dayWeeks.contains(it.dayOfWeek) }
                .toList()

            val professional = personRepository.getTOPersonById(toScheduler.professionalPersonId!!)

            val schedules = scheduleDates.map {
                TOScheduler(
                    academyMemberPersonId = toScheduler.academyMemberPersonId,
                    professionalPersonId = toScheduler.professionalPersonId,
                    scheduledDate = it,
                    start = toScheduler.start,
                    end = toScheduler.end,
                    professionalType = professional.toUser?.type!!,
                    situation = EnumSchedulerSituation.SCHEDULED,
                    compromiseType = EnumCompromiseType.RECURRENT,
                    observation = toScheduler.observation
                )
            }

            validateSchedulerConflictRecurrentCompromise(schedules)?.let(validations::add)

            if (validations.isEmpty()) {
                schedulerRepository.saveRecurrentScheduler(schedules)
            }
        }

        return validations
    }

    private fun validateCommonInfosRecurrentCompromise(toScheduler: TOScheduler): MutableList<FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>> {
        val validationResults = mutableListOf(
            validateMember(toScheduler),
            validateHourStart(toScheduler),
            validateHourEnd(toScheduler),
            validateObservation(toScheduler),
            validateHourPeriod(toScheduler)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private fun validateConfigRecurrentCompromise(toScheduler: TOScheduler, config: CompromiseRecurrentConfig): MutableList<FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>> {
        val validationResults = mutableListOf(
            validateDateStart(config),
            validateDateEnd(config),
            validateDatePeriod(config),
            validateDayOfWeeks(toScheduler, config)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private fun validateDateStart(config: CompromiseRecurrentConfig): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        return when {
            config.dateStart == null -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EnumValidatedCompromiseFields.DATE_START.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedCompromiseFields.DATE_START,
                    validationType = EnumCompromiseValidationTypes.REQUIRED_DATE_START,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun validateDateEnd(config: CompromiseRecurrentConfig): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        return when {
            config.dateEnd == null -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EnumValidatedCompromiseFields.DATE_END.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedCompromiseFields.DATE_END,
                    validationType = EnumCompromiseValidationTypes.REQUIRED_DATE_END,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun validateDatePeriod(config: CompromiseRecurrentConfig): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        if (config.dateStart == null || config.dateEnd == null) return null

        return when {
            config.dateStart.isAfter(config.dateEnd) || config.dateStart == config.dateEnd -> {
                val message = context.getString(R.string.save_compromise_invalid_date_period)

                FieldValidationError(
                    field = null,
                    validationType = EnumCompromiseValidationTypes.INVALID_DATE_PERIOD,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun validateDayOfWeeks(toScheduler: TOScheduler, config: CompromiseRecurrentConfig): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        val requiredFields = listOf(
            config.dateStart,
            config.dateEnd,
            toScheduler.academyMemberPersonId,
            toScheduler.start,
            toScheduler.end
        )

        if (requiredFields.any { it == null }) return null

        return when {
            config.dayWeeks.isEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EnumValidatedCompromiseFields.DAY_OF_WEEKS.labelResId)
                )

                FieldValidationError(
                    field = null,
                    validationType = EnumCompromiseValidationTypes.REQUIRED_DAY_OF_WEEKS,
                    message = message
                )
            }

            else -> null
        }
    }

    private suspend fun validateSchedulerConflictRecurrentCompromise(schedules: List<TOScheduler>): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        val member = personRepository.getTOPersonById(schedules.first().academyMemberPersonId!!)
        val conflicts = schedules.filter { scheduler ->
            schedulerRepository.getHasSchedulerConflict(
                schedulerId = scheduler.id,
                personId = scheduler.academyMemberPersonId!!,
                userType = member.toUser?.type!!,
                scheduledDate = scheduler.scheduledDate!!,
                start = scheduler.start!!,
                end = scheduler.end!!
            )
        }

        return if (conflicts.isNotEmpty()) {
            val formatedDates = conflicts.joinToString(separator = ", \n") {
                it.scheduledDate!!.format(EnumDateTimePatterns.DATE)
            }

            val message = context.getString(
                R.string.save_recurrent_compromise_conflicts_occurred,
                formatedDates
            )

            FieldValidationError(
                field = null,
                validationType = EnumCompromiseValidationTypes.RECURRENT_SCHEDULER_CONFLICT,
                message = message
            )
        } else {
            null
        }
    }
}

data class CompromiseRecurrentConfig(
    val dateStart: LocalDate? = null,
    val dateEnd: LocalDate? = null,
    val dayWeeks: List<DayOfWeek> = emptyList(),
)