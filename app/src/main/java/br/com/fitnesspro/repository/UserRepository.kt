package br.com.fitnesspro.repository

import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class UserRepository(
    private val personDAO: PersonDAO,
    private val userDAO: UserDAO
) {

    suspend fun savePerson(user: User, person: Person) = withContext(IO) {
        personDAO.save(user, person)
    }

    suspend fun hasUserWithEmail(email: String): Boolean = withContext(IO) {
        userDAO.hasUserWithEmail(email)
    }

    suspend fun hasUserWithCredentials(email: String, password: String): Boolean = withContext(IO) {
        userDAO.hasUserWithCredentials(email, password)
    }

    suspend fun authenticate(email: String, password: String) = withContext(IO) {
        userDAO.authenticate(email, password)
    }

    suspend fun getAuthenticatedTOPerson(): TOPerson? = withContext(IO) {
        val toUser = userDAO.getAuthenticatedUser()?.run {
            TOUser(
                id = id,
                email = email,
                password = password,
                type = type,
                active = active
            )
        }

        if (toUser == null) return@withContext null

        personDAO.findByUserId(toUser.id!!).run {
            TOPerson(
                id = id,
                name = name,
                birthDate = birthDate,
                phone = phone,
                toUser = toUser,
                active = active
            )
        }
    }

    suspend fun getAuthenticatedTOUser(): TOUser? = withContext(IO) {
        userDAO.getAuthenticatedUser()?.run {
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