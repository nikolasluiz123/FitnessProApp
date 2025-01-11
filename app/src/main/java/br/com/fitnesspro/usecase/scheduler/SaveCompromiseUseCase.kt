package br.com.fitnesspro.usecase.scheduler

import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.usecase.scheduler.enums.EnumValidatedCompromiseFields

class SaveCompromiseUseCase(
    private val uniqueCompromiseUseCase: SaveUniqueCompromiseUseCase,
    private val recurrentCompromiseUseCase: SaveRecurrentCompromiseUseCase,
    private val suggestionCompromiseUseCase: SaveCompromiseSuggestionUseCase
) {

    suspend fun execute(
        toScheduler: TOScheduler,
        type: EnumSchedulerType,
        recurrentConfig: CompromiseRecurrentConfig? = null
    ): MutableList<Pair<EnumValidatedCompromiseFields?, String>> {
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


