package br.com.fitnesspro.scheduler.usecase.scheduler

import android.content.Context
import br.com.android.ui.compose.components.fields.validation.ValidationError
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.CANCELLED
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.COMPLETED
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.CONFIRMED
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.SCHEDULED
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.to.TOScheduler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class ConfirmationSchedulerUseCase(
    private val context: Context,
    private val schedulerRepository: SchedulerRepository,
) {

    suspend operator fun invoke(toScheduler: TOScheduler, schedulerType: EnumSchedulerType): ValidationError? = withContext(IO) {
            val result = validate(toScheduler)

            if (result == null) {
                toScheduler.situation = if (toScheduler.situation == SCHEDULED) CONFIRMED else COMPLETED
                schedulerRepository.saveScheduler(toScheduler, schedulerType)
            }

            result
        }

    private fun validate(toScheduler: TOScheduler): ValidationError? {
        return if (toScheduler.situation in listOf(CANCELLED, COMPLETED, null)) {
            ValidationError(
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