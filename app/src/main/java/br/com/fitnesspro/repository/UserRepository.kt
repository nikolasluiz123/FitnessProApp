package br.com.fitnesspro.repository

import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
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

}