package br.com.fitnesspro.tests.ui.common

import android.util.Log
import br.com.fitnesspro.common.usecase.login.LoginUseCase
import br.com.fitnesspro.common.usecase.person.SavePersonUseCase
import br.com.fitnesspro.model.enums.EnumUserType.ACADEMY_MEMBER
import br.com.fitnesspro.model.enums.EnumUserType.NUTRITIONIST
import br.com.fitnesspro.model.enums.EnumUserType.PERSONAL_TRAINER
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject

@HiltAndroidTest
abstract class BaseAuthenticatedUITest: BaseUITests() {

    @Inject
    lateinit var savePersonUseCase: SavePersonUseCase

    @Inject
    lateinit var loginUseCase: LoginUseCase

    protected lateinit var toPersons: List<TOPerson>

    protected suspend fun prepareDatabaseWithPersons() {
        toPersons = listOf(
            TOPerson(
                name = "Personal Trainer",
                toUser = TOUser(
                    email = PERSONAL_EMAIL,
                    password = DEFAULT_PASSWORD,
                    type = PERSONAL_TRAINER
                ),
            ),
            TOPerson(
                name = "Nutritionist",
                toUser = TOUser(
                    email = NUTRITIONIST_EMAIL,
                    password = DEFAULT_PASSWORD,
                    type = NUTRITIONIST
                ),
            ),
            TOPerson(
                name = "Academy Member",
                toUser = TOUser(
                    email = MEMBER_EMAIL,
                    password = DEFAULT_PASSWORD,
                    type = ACADEMY_MEMBER
                )
            )
        )

        toPersons.forEach { toPerson ->
            val result = savePersonUseCase.execute(toPerson)

            Log.i(getTag(), "Saved Person With Credentials: ${toPerson.toUser?.email}, ${toPerson.toUser?.password}")

            if (result.isNotEmpty()) {
                Log.e(getTag(), "prepareUsersToLoginTest Error: ${result.map { it.validationType }}")
            }
        }
    }

    protected suspend fun authenticatePersonal() {
        val result = loginUseCase.execute(PERSONAL_EMAIL, DEFAULT_PASSWORD)

        if (result.isNotEmpty()) {
            Log.e(getTag(), "Personal Authentication Error: ${result.map { it.validationType }}")
        }
    }

    protected  suspend fun authenticateNutritionist() {
        val result = loginUseCase.execute(NUTRITIONIST_EMAIL, DEFAULT_PASSWORD)

        if (result.isNotEmpty()) {
            Log.e(getTag(), "Nutritionist Authentication Error: ${result.map { it.validationType }}")
        }
    }

    protected  suspend fun authenticateMember() {
        val result = loginUseCase.execute(MEMBER_EMAIL, DEFAULT_PASSWORD)

        if (result.isNotEmpty()) {
            Log.e(getTag(), "Member Authentication Error: ${result.map { it.validationType }}")
        }
    }

    companion object {
        const val MEMBER_EMAIL = "member@gmail.com"
        const val PERSONAL_EMAIL = "personal@gmail.com"
        const val NUTRITIONIST_EMAIL = "nutritionist@gmail.com"
        const val DEFAULT_PASSWORD = "teste123456"
    }
}