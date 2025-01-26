package br.com.fitnesspro.common.usecase.academy

import android.content.Context
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOPersonAcademyTime

class SavePersonAcademyTimeBatchUseCase(
    context: Context,
    academyRepository: AcademyRepository
): SavePersonAcademyTimeUseCase(context, academyRepository) {

    suspend fun executeInBatches(toPersonAcademyTimes: List<TOPersonAcademyTime>): MutableList<FieldValidationError<EnumValidatedAcademyFields, EnumAcademyValidationTypes>> {
        val validationsResults = mutableListOf<FieldValidationError<EnumValidatedAcademyFields, EnumAcademyValidationTypes>>()

        toPersonAcademyTimes.forEach {
            val result = getAllValidationResults(it)
            validationsResults.addAll(result)
        }

        if (validationsResults.isEmpty()) {
            academyRepository.savePersonAcademyTimeBatch(toPersonAcademyTimes)
        }

        return validationsResults
    }
}