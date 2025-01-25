package br.com.fitnesspro.scheduler.usecase.scheduler

import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields
import br.com.fitnesspro.to.TOScheduler

class SaveCompromiseUseCase(
    private val uniqueCompromiseUseCase: SaveUniqueCompromiseUseCase,
    private val recurrentCompromiseUseCase: SaveRecurrentCompromiseUseCase,
    private val suggestionCompromiseUseCase: SaveCompromiseSuggestionUseCase
) {

    suspend fun execute(
        toScheduler: TOScheduler,
        type: EnumSchedulerType,
        recurrentConfig: CompromiseRecurrentConfig? = null
    ): MutableList<FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>> {
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


