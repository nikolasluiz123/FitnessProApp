package br.com.fitnesspro.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.R
import br.com.fitnesspro.repository.SchedulerRepository
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.usecase.scheduler.enums.EnumValidatedCompromiseFields
import java.time.LocalDate
import java.time.LocalTime

abstract class SaveCompromiseCommonUseCase(
    protected val context: Context,
    protected val schedulerRepository: SchedulerRepository,
    protected val userRepository: UserRepository
) {

    protected fun validateMember(scheduler: TOScheduler): Pair<EnumValidatedCompromiseFields, String>? {
        val validationPair = when {
            scheduler.academyMemberPersonId.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedCompromiseFields.MEMBER.labelResId)
                )

                Pair(EnumValidatedCompromiseFields.MEMBER, message)
            }

            else -> null
        }

        return validationPair
    }

    protected fun validateHourStart(scheduler: TOScheduler): Pair<EnumValidatedCompromiseFields, String>? {
        val actualHour = LocalTime.now()
        val start = scheduler.start

        val validationPair = when {
            scheduler.start == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedCompromiseFields.HOUR_START.labelResId)
                )

                Pair(EnumValidatedCompromiseFields.HOUR_START, message)
            }

            start!! < actualHour && scheduler.scheduledDate == LocalDate.now() -> {
                val message = context.getString(R.string.save_compromise_start_hour_require_future)

                Pair(EnumValidatedCompromiseFields.HOUR_START, message)
            }

            start <= actualHour.plusHours(1) && scheduler.scheduledDate == LocalDate.now() -> {
                val message = context.getString(R.string.save_compromise_start_hour_require_antecedence)

                Pair(EnumValidatedCompromiseFields.HOUR_START, message)
            }

            else -> null
        }

        return validationPair
    }

    protected fun validateHourEnd(scheduler: TOScheduler): Pair<EnumValidatedCompromiseFields, String>? {
        val validationPair = when {
            scheduler.end == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedCompromiseFields.HOUR_END.labelResId)
                )

                Pair(EnumValidatedCompromiseFields.HOUR_END, message)
            }

            else -> null
        }

        return validationPair
    }

    protected fun validateHourPeriod(scheduler: TOScheduler): Pair<EnumValidatedCompromiseFields?, String>? {
        if (scheduler.start == null || scheduler.end == null) return null

        val validationPair = when {
            scheduler.start!!.isAfter(scheduler.end!!) || scheduler.start == scheduler.end -> {
                val message = context.getString(R.string.save_compromise_invalid_hour_period)

                Pair(null, message)
            }

            else -> null
        }

        return validationPair
    }
}