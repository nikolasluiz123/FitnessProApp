package br.com.fitnesspro.scheduler.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.scheduler.R
import java.time.DayOfWeek
import java.time.LocalDate

class SaveRecurrentCompromiseUseCase(
    context: Context,
    schedulerRepository: SchedulerRepository,
    userRepository: UserRepository
): SaveCompromiseCommonUseCase(context, schedulerRepository, userRepository) {

    suspend fun saveRecurrentCompromise(toScheduler: TOScheduler, config: CompromiseRecurrentConfig): MutableList<Pair<EnumValidatedCompromiseFields?, String>> {
        val validations = mutableListOf<Pair<EnumValidatedCompromiseFields?, String>>()
        validations.addAll(validateCommonInfosRecurrentCompromise(toScheduler))
        validations.addAll(validateConfigRecurrentCompromise(config))

        if (validations.isEmpty()) {
            val scheduleDates = generateSequence(config.dateStart!!) { it.plusDays(1) }
                .takeWhile { it <= config.dateEnd!! }
                .filter { config.dayWeeks.contains(it.dayOfWeek) }
                .toList()

            val professional = userRepository.getTOPersonById(toScheduler.professionalPersonId!!)

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

    private fun validateCommonInfosRecurrentCompromise(toScheduler: TOScheduler): MutableList<Pair<EnumValidatedCompromiseFields?, String>> {
        val validationResults = mutableListOf(
            validateMember(toScheduler),
            validateHourStart(toScheduler),
            validateHourEnd(toScheduler),
            validateHourPeriod(toScheduler)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private fun validateConfigRecurrentCompromise(config: CompromiseRecurrentConfig): MutableList<Pair<EnumValidatedCompromiseFields?, String>> {
        val validationResults = mutableListOf(
            validateDateStart(config),
            validateDateEnd(config),
            validateDatePeriod(config),
            validateDayOfWeeks(config)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private fun validateDateStart(config: CompromiseRecurrentConfig): Pair<EnumValidatedCompromiseFields?, String>? {
        val validationPair = when {
            config.dateStart == null -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EnumValidatedCompromiseFields.DATE_START.labelResId)
                )

                Pair(EnumValidatedCompromiseFields.DATE_START, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateDateEnd(config: CompromiseRecurrentConfig): Pair<EnumValidatedCompromiseFields?, String>? {
        val validationPair = when {
            config.dateEnd == null -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EnumValidatedCompromiseFields.DATE_END.labelResId)
                )

                Pair(EnumValidatedCompromiseFields.DATE_END, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateDatePeriod(config: CompromiseRecurrentConfig): Pair<EnumValidatedCompromiseFields?, String>? {
        if (config.dateStart == null || config.dateEnd == null) return null

        val validationPair = when {
            config.dateStart.isAfter(config.dateEnd) || config.dateStart == config.dateEnd -> {
                val message = context.getString(R.string.save_compromise_invalid_date_period)

                Pair(null, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateDayOfWeeks(config: CompromiseRecurrentConfig): Pair<EnumValidatedCompromiseFields?, String>? {
        val validationPair = when {
            config.dayWeeks.isEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EnumValidatedCompromiseFields.DAY_OF_WEEKS.labelResId)
                )

                Pair(null, message)
            }

            else -> null
        }

        return validationPair
    }

    private suspend fun validateSchedulerConflictRecurrentCompromise(schedules: List<TOScheduler>): Pair<EnumValidatedCompromiseFields?, String>? {
        val member = userRepository.getTOPersonById(schedules.first().academyMemberPersonId!!)
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

            Pair(null, message)
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