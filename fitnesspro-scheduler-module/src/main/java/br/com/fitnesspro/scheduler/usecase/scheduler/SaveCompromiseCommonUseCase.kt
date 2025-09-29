package br.com.fitnesspro.scheduler.usecase.scheduler

import android.content.Context
import br.com.android.ui.compose.components.fields.validation.FieldValidationError
import br.com.core.utils.extensions.dateNow
import br.com.core.utils.extensions.timeNow
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.HOUR_END
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.HOUR_START
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.MEMBER
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.OBSERVATION
import br.com.fitnesspro.to.TOScheduler

abstract class SaveCompromiseCommonUseCase(
    protected val context: Context,
    protected val schedulerRepository: SchedulerRepository,
    protected val userRepository: UserRepository
) {

    protected fun validateMember(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields>? {
        return when {
            scheduler.academyMemberPersonId.isNullOrEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(MEMBER.labelResId)
                )

                FieldValidationError(
                    field = MEMBER,
                    message = message
                )
            }

            else -> null
        }
    }

    protected fun validateHourStart(scheduler: TOScheduler, recurrent: Boolean): FieldValidationError<EnumValidatedCompromiseFields>? {
        val startLocalTime = scheduler.dateTimeStart?.toLocalTime()
        val startZone = scheduler.dateTimeStart?.toZonedDateTime()?.zone
        val startLocalDate = scheduler.dateTimeStart?.toLocalDate()

        val timeNow = startZone?.let(::timeNow)
        val dateNow = startZone?.let(::dateNow)

        val validation = when {
            scheduler.dateTimeStart == null -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(HOUR_START.labelResId)
                )

                FieldValidationError(
                    field = HOUR_START,
                    message = message
                )
            }

            startLocalTime!! < timeNow!! && startLocalDate == dateNow && recurrent.not() -> {
                val message = context.getString(R.string.save_compromise_start_hour_require_future)

                FieldValidationError(
                    field = HOUR_START,
                    message = message
                )
            }

            startLocalTime <= timeNow.plusHours(1) && startLocalDate == dateNow && recurrent.not() -> {
                val message = context.getString(R.string.save_compromise_start_hour_require_antecedence)

                FieldValidationError(
                    field = HOUR_START,
                    message = message
                )
            }

            else -> null
        }

        return validation
    }

    protected fun validateHourEnd(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields>? {
        return when {
            scheduler.dateTimeEnd == null -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(HOUR_END.labelResId)
                )

                FieldValidationError(
                    field = HOUR_END,
                    message = message
                )
            }

            else -> null
        }
    }

    protected fun validateHourPeriod(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields>? {
        if (scheduler.dateTimeStart == null || scheduler.dateTimeEnd == null) return null

        return when {
            scheduler.dateTimeStart!!.isAfter(scheduler.dateTimeEnd!!) || scheduler.dateTimeStart == scheduler.dateTimeEnd -> {
                val message = context.getString(R.string.save_compromise_invalid_hour_period)

                FieldValidationError(
                    field = null,
                    message = message
                )
            }

            else -> null
        }
    }

    protected fun validateObservation(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields>? {
        val observation = scheduler.observation?.trim()

        return if (!observation.isNullOrEmpty() && observation.length > OBSERVATION.maxLength) {

            val message = context.getString(
                br.com.fitnesspro.common.R.string.validation_msg_field_with_max_length,
                context.getString(OBSERVATION.labelResId),
                OBSERVATION.maxLength
            )

            FieldValidationError(
                field = OBSERVATION,
                message = message
            )
        } else {
            scheduler.observation = observation
            null
        }
    }
}