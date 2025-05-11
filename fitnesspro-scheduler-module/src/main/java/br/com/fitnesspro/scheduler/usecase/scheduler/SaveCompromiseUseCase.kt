package br.com.fitnesspro.scheduler.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.core.exceptions.NoLoggingException
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields
import br.com.fitnesspro.to.TOScheduler

class SaveCompromiseUseCase(
    private val context: Context,
    private val uniqueCompromiseUseCase: SaveUniqueCompromiseUseCase,
    private val recurrentCompromiseUseCase: SaveRecurrentCompromiseUseCase,
    private val suggestionCompromiseUseCase: SaveCompromiseSuggestionUseCase
) {

    suspend fun execute(
        toScheduler: TOScheduler,
        type: EnumSchedulerType,
        recurrentConfig: CompromiseRecurrentConfig? = null
    ): MutableList<FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>> {
        if (!context.isNetworkAvailable()) {
            throw NoLoggingException(context.getString(R.string.network_required_save_schedule_message))
        }

        return when (type) {
            EnumSchedulerType.SUGGESTION -> {
                suggestionCompromiseUseCase.saveCompromiseSuggestion(toScheduler)
            }

            EnumSchedulerType.UNIQUE -> {
                uniqueCompromiseUseCase.saveUniqueCompromise(toScheduler)
            }

            EnumSchedulerType.RECURRENT -> {
                recurrentCompromiseUseCase.saveRecurrentCompromise(toScheduler, recurrentConfig!!)
            }
        }
    }
}


