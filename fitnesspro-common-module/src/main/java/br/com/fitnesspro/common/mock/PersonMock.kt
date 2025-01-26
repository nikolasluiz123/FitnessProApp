package br.com.fitnesspro.common.mock

import android.util.Log
import br.com.fitnesspro.common.usecase.person.SavePersonBatchUseCase
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser
import com.github.javafaker.Faker
import java.time.ZoneId

class PersonMockHelper(
    private val savePersonBatchUseCase: SavePersonBatchUseCase,
    private val faker: Faker
) {

    private val users = mutableListOf<TOUser>()
    private var lastUserIndex = 0

    private val persons = mutableListOf<TOPerson>()
    private var lastPersonIndex = 0

    suspend fun executeInsertsPersonMock() {
        while (lastPersonIndex < MAX_PERSONS) {
            populateUsersList()
            populatePersonsList()

            executeUseCase()

            lastUserIndex += BATCH_SIZE
            lastPersonIndex += BATCH_SIZE
        }
    }

    private fun getTOUser(i: Int): TOUser {
        val type = EnumUserType.entries.toTypedArray().random()

        return TOUser(
            email = "user$i@gmail.com",
            password = "123456",
            type = type
        )
    }

    private fun getTOPerson(user: TOUser): TOPerson {
        val professionals = listOf(EnumUserType.PERSONAL_TRAINER, EnumUserType.NUTRITIONIST)

        val phone = if (user.type in professionals) {
            val value = faker.phoneNumber().phoneNumber()

            value.replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "")
        } else {
            null
        }

        return TOPerson(
            name = faker.name().fullName(),
            birthDate = faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            phone = phone,
            toUser = user
        )
    }

    private suspend fun executeUseCase() {
        val validationResults = savePersonBatchUseCase.executeInclusionBatch(persons)
        val batchNumber = lastPersonIndex / BATCH_SIZE

        if (validationResults.isNotEmpty()) {
            Log.e(TAG, "Nao foi possivel salvar o batch $batchNumber")

            validationResults.forEach {
                Log.e(TAG, "${it.field?.name}: ${it.message}")
            }
        } else {
            Log.i(TAG, "Batch $batchNumber salvo com sucesso")
        }
    }

    private fun populateUsersList() {
        users.clear()

        for (i in lastUserIndex until lastUserIndex + BATCH_SIZE) {
            users.add(getTOUser(i))
        }

        Log.i(TAG, "Populou a lista de usuarios com ${users.size} usuarios")
    }

    private fun populatePersonsList() {
        persons.clear()

        for (i in 0 until BATCH_SIZE) {
            val globalIndex = lastPersonIndex + i
            if (globalIndex < MAX_PERSONS) {
                persons.add(getTOPerson(users[i]))
            }
        }

        Log.i(TAG, "Populou a lista de pessoas com ${persons.size} pessoas")
    }
    companion object {
        private const val TAG = "PERSON MOCK"
        private const val MAX_PERSONS = 5000
        private const val BATCH_SIZE = 100
    }
}