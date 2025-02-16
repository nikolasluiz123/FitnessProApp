package br.com.fitnesspro.scheduler.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.core.validation.ValidationError
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.CANCELLED
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.COMPLETED
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.CONFIRMED
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.SCHEDULED
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.INVALID_SITUATION_FOR_CONFIRMATION
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.to.TOScheduler

class ConfirmationSchedulerUseCase(
    private val context: Context,
    private val schedulerRepository: SchedulerRepository,
) {

    suspend operator fun invoke(toScheduler: TOScheduler, schedulerType: EnumSchedulerType): ValidationError<EnumCompromiseValidationTypes>? {
        val result = validate(toScheduler)

        if (result == null) {
            toScheduler.situation = if (toScheduler.situation == SCHEDULED) CONFIRMED else COMPLETED
            schedulerRepository.saveScheduler(toScheduler, schedulerType)
        }

        return result
    }

    private fun validate(toScheduler: TOScheduler): ValidationError<EnumCompromiseValidationTypes>? {
        return if (toScheduler.situation in listOf(CANCELLED, COMPLETED, null)) {
            ValidationError(
                validationType = INVALID_SITUATION_FOR_CONFIRMATION,
                message = context.getString(
                    R.string.compromise_confirmation_invalid_situation_message,
                    context.getString(R.string.label_confirmacao),
                    SCHEDULED.getLabel(context)!!,
                    CONFIRMED.getLabel(context)!!,
                    context.getString(R.string.label_confirmado)
                )
            )
        } else {
            null
        }
    }
}