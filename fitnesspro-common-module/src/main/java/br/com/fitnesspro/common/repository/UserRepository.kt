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
    suspend fun savePerson(user: User, person: Person) = withContext(IO) {
        userDAO.save(user)
        personDAO.save(person)
    }

    @Transaction
    suspend fun savePersonBatch(users: List<User>, persons: List<Person>) = withContext(IO) {
        userDAO.saveBatch(users)
        personDAO.saveBatch(persons)
    }

    suspend fun hasUserWithEmail(email: String, userId: String): Boolean = withContext(IO) {
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

}