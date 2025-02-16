package br.com.fitnesspro.scheduler.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.validation.ValidationError
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.CANCELLED
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.COMPLETED
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.CONFIRMED
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.SCHEDULED
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.INVALID_SITUATION_FOR_INACTIVATION
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.to.TOScheduler

class InactivateSchedulerUseCase(
    private val context: Context,
    private val schedulerRepository: SchedulerRepository,
) {

    suspend operator fun invoke(toScheduler: TOScheduler, schedulerType: EnumSchedulerType): ValidationError<EnumCompromiseValidationTypes>? {
        val result = validate(toScheduler)

        if (result == null) {
            toScheduler.apply {
                active = false
                canceledDate = dateTimeNow()
                situation = CANCELLED

                schedulerRepository.saveScheduler(this, schedulerType)
            }
        }

        return result
    }

    private fun validate(toScheduler: TOScheduler): ValidationError<EnumCompromiseValidationTypes>? {
        val invalidSituations = listOf(CANCELLED, COMPLETED, null)

        return if (toScheduler.situation in invalidSituations) {
            ValidationError(
                validationType = INVALID_SITUATION_FOR_INACTIVATION,
                message = context.getString(
                    R.string.compromise_confirmation_invalid_situation_message,
                    context.getString(R.string.label_cancelamento),
                    SCHEDULED.getLabel(context)!!,
                    CONFIRMED.getLabel(context)!!,
                    context.getString(R.string.label_cancelado)
                )
            )
        } else {
            null
        }
    }

}