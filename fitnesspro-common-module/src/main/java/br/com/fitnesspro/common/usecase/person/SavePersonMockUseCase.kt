package br.com.fitnesspro.common.usecase.person

import android.content.Context
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.core.security.HashHelper
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.to.TOPerson

class SavePersonMockUseCase(
    context: Context,
    userRepository: UserRepository,
    saveSchedulerConfigUseCase: SaveSchedulerConfigUseCase
): SavePersonUseCase(
    context = context,
    userRepository = userRepository,
    saveSchedulerConfigUseCase = saveSchedulerConfigUseCase
) {
    suspend fun executeInclusionBatch(toPersons: List<TOPerson>): MutableList<Pair<EnumValidatedPersonFields, String>> {
        val users = toPersons.map {
            User(
                email = it.toUser?.email,
                password = it.toUser?.password,
                type = it.toUser?.type
            )
        }

        val persons = toPersons.mapIndexed { index, toPerson ->
            Person(
                name = toPerson.name,
                birthDate = toPerson.birthDate,
                phone = toPerson.phone,
                userId = users[index].id
            )
        }

        val validationResults = mutableListOf<Pair<EnumValidatedPersonFields, String>>()

        users.forEach {
            val result = validateUser(it)

            if (result.isEmpty()) {
                it.password = HashHelper.applyHash(it.password!!)
            }

            validationResults.addAll(result)
        }

        persons.forEach {
            validationResults.addAll(validatePerson(it))
        }

        if (validationResults.isEmpty()) {
            userRepository.savePersonBatch(users, persons)
            saveSchedulerConfigUseCase.createConfigBatch(persons.map { it.id })
        }

        return validationResults
    }
}