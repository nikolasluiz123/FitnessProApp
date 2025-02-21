package br.com.fitnesspro.tests.integration.room

import androidx.paging.PagingSource
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.enums.EnumUserType.ACADEMY_MEMBER
import br.com.fitnesspro.model.enums.EnumUserType.NUTRITIONIST
import br.com.fitnesspro.model.enums.EnumUserType.PERSONAL_TRAINER
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.tuple.PersonTuple
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SavePersonUseCaseIntegrationRoomTests {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var userDAO: UserDAO

    @Inject
    lateinit var personDAO: PersonDAO

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun should_return_true_when_has_user_using_this_email(): Unit = runBlocking {
        val email = "teste@gmail.com"

        val registredUser = User(
            email = email,
            password = "teste123456",
            type = PERSONAL_TRAINER
        )

        userDAO.insert(registredUser)

        val otherUser = User(
            email = email,
            password = "TESTANDO123",
            type = NUTRITIONIST
        )

        val hasUser = userDAO.hasUserWithEmail(registredUser.email!!, otherUser.id)

        hasUser.shouldBeTrue()
    }

    @Test
    fun should_return_false_when_has_not_user_using_this_email(): Unit = runBlocking {
        val userTryRegister = User(
            email = "teste@gmail.com",
            password = "teste123456",
            type = PERSONAL_TRAINER
        )

        val hasUser = userDAO.hasUserWithEmail(userTryRegister.email!!, userTryRegister.id)

        hasUser.shouldBeFalse()
    }

    @Test
    fun should_return_true_when_has_user_with_credentials(): Unit = runBlocking {
        val email = "teste@gmail.com"
        val password = "teste123456"

        val registredUser = User(
            email = email,
            password = password,
            type = PERSONAL_TRAINER
        )

        userDAO.insert(registredUser)

        val hasUser = userDAO.hasUserWithCredentials(email, password)

        hasUser.shouldBeTrue()
    }

    @Test
    fun should_return_false_when_has_not_user_with_credentials(): Unit = runBlocking {
        val email = "teste@gmail.com"
        val password = "teste123456"

        val hasUser = userDAO.hasUserWithCredentials(email, password)

        hasUser.shouldBeFalse()
    }

    @Test
    fun should_logout_authenticated_user_when_another_user_authenticate(): Unit = runBlocking {
        val userAuthenticated = User(
            email = "teste@gmail.com",
            password = "teste123456",
            type = PERSONAL_TRAINER,
            authenticated = true
        )

        val otherUser = User(
            email = "teste2@gmail.com",
            password = "teste2123456",
            type = NUTRITIONIST,
            authenticated = false
        )

        userDAO.insertBatch(listOf(userAuthenticated, otherUser))
        userDAO.authenticate(otherUser.email!!, otherUser.password!!)

        userDAO.findById(otherUser.id)!!.authenticated.shouldBeTrue()
        userDAO.findById(userAuthenticated.id)!!.authenticated.shouldBeFalse()
    }

    @Test
    fun should_return_only_active_personal_trainers_when_get_personal_trainers(): Unit = runBlocking {
        savePersonList()

        getPersons(listOf(element = PERSONAL_TRAINER), simpleFilter = "").forEach {
            val active = personDAO.findPersonById(it.id).active

            it.userType shouldBe PERSONAL_TRAINER
            active shouldBe true
        }
    }

    @Test
    fun should_return_only_active_nutritionists_when_get_nutritionists(): Unit = runBlocking {
        savePersonList()

        getPersons(listOf(element = NUTRITIONIST), simpleFilter = "").forEach {
            val active = personDAO.findPersonById(it.id).active

            it.userType shouldBe NUTRITIONIST
            active shouldBe true
        }
    }

    @Test
    fun should_return_only_active_academy_members_when_get_academy_members(): Unit = runBlocking {
        savePersonList()

        getPersons(listOf(element = ACADEMY_MEMBER), simpleFilter = "").forEach {
            val active = personDAO.findPersonById(it.id).active

            it.userType shouldBe ACADEMY_MEMBER
            active shouldBe true
        }
    }

    @Test
    fun should_return_persons_with_filtered_name_when_quick_filter_informed(): Unit = runBlocking {
        savePersonList()

        getPersons(
            types = listOf(PERSONAL_TRAINER, NUTRITIONIST, ACADEMY_MEMBER),
            simpleFilter = "Personal"
        ).forEach {
            val active = personDAO.findPersonById(it.id).active

            it.name shouldContain "Personal"
            active shouldBe true
        }
    }

    private suspend fun getPersons(types: List<EnumUserType>, simpleFilter: String = ""): List<PersonTuple> {
        val pagingSource = personDAO.getPersonsWithUserType(
            types = types,
            simpleFilter = simpleFilter,
            personsForSchedule = false
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                null,
                20,
                false
            )
        ) as PagingSource.LoadResult.Page

        return result.data
    }

    private suspend fun savePersonList() {
        val users = listOf(
            User(
                email = "teste@gmail.com",
                password = "teste123456",
                type = PERSONAL_TRAINER,
            ),
            User(
                email = "teste2@gmail.com",
                password = "teste2123456",
                type = NUTRITIONIST,
            ),
            User(
                email = "teste3@gmail.com",
                password = "teste3123456",
                type = ACADEMY_MEMBER,
            ),
            User(
                email = "teste4@gmail.com",
                password = "teste4123456",
                type = PERSONAL_TRAINER,
                active = false
            )
        )

        val persons = listOf(
            Person(
                id = "1",
                name = "Personal Trainer",
                userId = users[0].id,
            ),
            Person(
                id = "2",
                name = "Nutritionist",
                userId = users[1].id,
            ),
            Person(
                id = "3",
                name = "Academy Member",
                userId = users[2].id,
            ),
            Person(
                id = "4",
                name = "Inactive Personal Trainer",
                userId = users[3].id,
            )
        )

        userDAO.insertBatch(users)
        personDAO.insertBatch(persons)
    }
}