package br.com.fitnesspro.scheduler.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.timeNow
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.INVALID_HOUR_PERIOD
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.REQUIRED_HOUR_END
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.REQUIRED_HOUR_START
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.REQUIRED_MEMBER
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.START_HOUR_IN_PAST_TODAY
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.START_HOUR_WITHOUT_ONE_HOUR_ANTECEDENCE_TODAY
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

    protected fun validateMember(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        return when {
            scheduler.academyMemberPersonId.isNullOrEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(MEMBER.labelResId)
                )

                FieldValidationError(
                    field = MEMBER,
                    validationType = REQUIRED_MEMBER,
                    message = message
                )
            }

            else -> null
        }
    }

    protected fun validateHourStart(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        val actualHour = timeNow()
        val start = scheduler.start

        return when {
            scheduler.start == null -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(HOUR_START.labelResId)
                )

                FieldValidationError(
                    field = HOUR_START,
                    validationType = REQUIRED_HOUR_START,
                    message = message
                )
            }

            start!! < actualHour && scheduler.scheduledDate == dateNow() -> {
                val message = context.getString(R.string.save_compromise_start_hour_require_future)

                FieldValidationError(
                    field = HOUR_START,
                    validationType = START_HOUR_IN_PAST_TODAY,
                    message = message
                )
            }

            start <= actualHour.plusHours(1) && scheduler.scheduledDate == dateNow() -> {
                val message = context.getString(R.string.save_compromise_start_hour_require_antecedence)

                FieldValidationError(
                    field = HOUR_START,
                    validationType = START_HOUR_WITHOUT_ONE_HOUR_ANTECEDENCE_TODAY,
                    message = message
                )
            }

            else -> null
        }
    }

    protected fun validateHourEnd(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        return when {
            scheduler.end == null -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(HOUR_END.labelResId)
                )

                FieldValidationError(
                    field = HOUR_END,
                    validationType = REQUIRED_HOUR_END,
                    message = message
                )
            }

            else -> null
        }
    }

    protected fun validateHourPeriod(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        if (scheduler.start == null || scheduler.end == null) return null

        return when {
            scheduler.start!!.isAfter(scheduler.end!!) || scheduler.start == scheduler.end -> {
                val message = context.getString(R.string.save_compromise_invalid_hour_period)

                FieldValidationError(
                    field = null,
                    validationType = INVALID_HOUR_PERIOD,
                    message = message
                )
            }

            else -> null
        }
    }

    protected fun validateObservation(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        val observation = scheduler.observation?.trim()

        return if (!observation.isNullOrEmpty() && observation.length > OBSERVATION.maxLength) {

            val message = context.getString(
                br.com.fitnesspro.common.R.string.validation_msg_field_with_max_length,
                context.getString(OBSERVATION.labelResId),
                OBSERVATION.maxLength
            )

            FieldValidationError(
                field = OBSERVATION,
                validationType = EnumCompromiseValidationTypes.MAX_LENGTH_OBSERVATION,
                message = message
            )
        } else {
            scheduler.observation = observation
            null
        }
    }
}