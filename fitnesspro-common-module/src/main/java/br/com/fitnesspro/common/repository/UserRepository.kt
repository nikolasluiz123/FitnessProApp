package br.com.fitnesspro.common.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Transaction
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser
import br.com.fitnesspro.tuple.PersonTuple
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class UserRepository(
    private val personDAO: PersonDAO,
    private val userDAO: UserDAO
) {

    @Transaction
    suspend fun savePerson(toPerson: TOPerson) = withContext(IO) {
        val user = toPerson.toUser!!.getUser()
        val person = toPerson.getPerson()

        toPerson.toUser?.id = user.id
        toPerson.id = person.id

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

    suspend fun authenticate(email: String, password: String) = withContext(IO) {
        userDAO.authenticate(email, password)
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

    suspend fun findUserById(userId: String): User = withContext(IO) {
        userDAO.findById(userId)
    }

    suspend fun findPersonById(personId: String): Person = withContext(IO) {
        personDAO.findPersonById(personId)
    }

    fun getListTOPersonWithUserType(types: List<EnumUserType>, simpleFilter: String): Pager<Int, PersonTuple> {
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
            userDAO.findById(id!!).copy(
                email = email,
                password = password,
                type = type,
                active = active
            )
        }
    }

    suspend fun logout() = withContext(IO) {
        userDAO.logoutAll()
    }
}