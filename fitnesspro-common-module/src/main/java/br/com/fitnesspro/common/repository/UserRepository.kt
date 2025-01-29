package br.com.fitnesspro.common.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Transaction
import br.com.fitnesspro.firebase.api.authentication.DefaultAuthenticationService
import br.com.fitnesspro.firebase.api.authentication.GoogleAuthenticationService
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser
import br.com.fitnesspro.tuple.PersonTuple
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class UserRepository(
    private val personDAO: PersonDAO,
    private val userDAO: UserDAO,
    private val defaultAuthenticationService: DefaultAuthenticationService,
    private val googleAuthenticationService: GoogleAuthenticationService
) {

    @Transaction
    suspend fun savePerson(toPerson: TOPerson) = withContext(IO) {
        val user = toPerson.toUser!!.getUser()
        val person = toPerson.getPerson()
        val existentUser = userDAO.findById(user.id)

        if (existentUser == null) {
            defaultAuthenticationService.register(
                email = user.email!!,
                password = user.password!!
            )
        } else {
            defaultAuthenticationService.updateUserInfos(user)
        }

        userDAO.save(user)
        personDAO.save(person)
    }

    @Transaction
    suspend fun savePersonBatch(toPersons: List<TOPerson>) = withContext(IO) {
        val users = toPersons.map { it.toUser!!.getUser() }
        val persons = toPersons.map { it.getPerson() }

        userDAO.saveBatch(users)
        personDAO.saveBatch(persons)
    }

    suspend fun hasUserWithEmail(email: String, userId: String?): Boolean = withContext(IO) {
        userDAO.hasUserWithEmail(email, userId)
    }

    suspend fun hasUserWithCredentials(email: String, password: String): Boolean = withContext(IO) {
        userDAO.hasUserWithCredentials(email, password)
    }

    suspend fun authenticate(email: String, password: String): Unit = withContext(IO) {
        userDAO.authenticate(email, password)

        try {
            defaultAuthenticationService.authenticate(email, password)
        } catch (ex: FirebaseAuthInvalidCredentialsException) {
            defaultAuthenticationService.register(email, password)
            defaultAuthenticationService.authenticate(email, password)
        }
    }

    suspend fun signInWithGoogle(): AuthResult? = withContext(IO) {
        googleAuthenticationService.signIn()
    }

    suspend fun getTOPersonById(personId: String): TOPerson = withContext(IO) {
        personDAO.findPersonById(personId).getTOPerson()!!
    }

    suspend fun getAuthenticatedTOPerson(): TOPerson? = withContext(IO) {
        val toUser = userDAO.getAuthenticatedUser()?.getTOUser() ?: return@withContext null
        personDAO.findPersonByUserId(toUser.id!!).getTOPerson()
    }

    suspend fun getAuthenticatedTOUser(): TOUser? = withContext(IO) {
        userDAO.getAuthenticatedUser()?.getTOUser()
    }

    suspend fun findUserById(userId: String): User? = withContext(IO) {
        userDAO.findById(userId)
    }

    suspend fun findUserByEmail(email: String): User? = withContext(IO) {
        userDAO.findByEmail(email)
    }

    suspend fun findPersonById(personId: String): Person = withContext(IO) {
        personDAO.findPersonById(personId)
    }

    fun getListTOPersonWithUserType(
        types: List<EnumUserType>,
        simpleFilter: String
    ): Pager<Int, PersonTuple> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                personDAO.getPersonsWithUserType(
                    types = types,
                    simpleFilter = simpleFilter
                )
            }
        )
    }

    private suspend fun Person?.getTOPerson(): TOPerson? {
        return this?.run {
            TOPerson(
                id = id,
                name = name,
                birthDate = birthDate,
                phone = phone,
                toUser = userDAO.findByPersonId(id).getTOUser(),
                active = active
            )
        }
    }

    private fun User?.getTOUser(): TOUser? {
        return this?.run {
            TOUser(
                id = id,
                email = email,
                password = password,
                type = type,
                active = active
            )
        }
    }

    private suspend fun TOPerson.getPerson(): Person {
        return if (id == null) {
            val model = Person(
                name = name,
                birthDate = birthDate,
                phone = phone,
                userId = toUser?.id,
                active = active
            )

            this.id = model.id

            model
        } else {
            personDAO.findPersonById(id!!).copy(
                name = name,
                birthDate = birthDate,
                phone = phone,
                active = active
            )
        }
    }

    private suspend fun TOUser.getUser(): User {
        return if (id == null) {
            val model = User(
                email = email,
                password = password,
                type = type,
                active = active
            )

            this.id = model.id

            model
        } else {
            userDAO.findById(id!!)!!.copy(
                email = email,
                password = password,
                type = type,
                active = active
            )
        }
    }

    suspend fun logout() = withContext(IO) {
        userDAO.logoutAll()
        defaultAuthenticationService.logout()
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}