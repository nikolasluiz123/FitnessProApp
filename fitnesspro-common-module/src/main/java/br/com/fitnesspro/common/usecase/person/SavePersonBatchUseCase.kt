package br.com.fitnesspro.common.usecase.person

import android.content.Context
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.core.security.HashHelper
import br.com.fitnesspro.core.security.IPasswordHasher
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOPerson

class SavePersonBatchUseCase(
    context: Context,
    userRepository: UserRepository,
    saveSchedulerConfigUseCase: SaveSchedulerConfigUseCase,
    passwordHasher: IPasswordHasher
): SavePersonUseCase(
    context = context,
    userRepository = userRepository,
    saveSchedulerConfigUseCase = saveSchedulerConfigUseCase,
    passwordHasher = passwordHasher
) {
    suspend fun executeInclusionBatch(toPersons: List<TOPerson>): MutableList<FieldValidationError<EnumValidatedPersonFields, EnumPersonValidationTypes>> {
        val validationResults = mutableListOf<FieldValidationError<EnumValidatedPersonFields, EnumPersonValidationTypes>>()

        toPersons.forEach {
            val result = validateUser(it)
            result.addAll(validatePerson(it))

            if (result.isEmpty()) {
                it.toUser?.password = HashHelper.applyHash(it.toUser?.password!!)
            }

            validationResults.addAll(result)
        }

        if (validationResults.isEmpty()) {
            userRepository.savePersonBatch(toPersons)
            saveSchedulerConfigUseCase.createConfigBatch(toPersons.map { it.id!! })
        }

        return validationResults
    }
}