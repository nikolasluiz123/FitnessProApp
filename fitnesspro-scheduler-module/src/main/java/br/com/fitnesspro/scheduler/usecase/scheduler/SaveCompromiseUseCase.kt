package br.com.fitnesspro.scheduler.usecase.scheduler

import android.content.Context
import br.com.android.firebase.toolkit.crashlytics.exception.NoLoggingException
import br.com.android.ui.compose.components.fields.validation.FieldValidationError
import br.com.core.android.utils.extensions.isNetworkAvailable
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields
import br.com.fitnesspro.to.TOScheduler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

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
    ): MutableList<FieldValidationError<EnumValidatedCompromiseFields>> = withContext(IO) {
        if (!context.isNetworkAvailable()) {
            throw NoLoggingException(context.getString(R.string.network_required_save_schedule_message))
        }

        when (type) {
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


