package br.com.fitnesspro.common.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Transaction
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspro.firebase.api.authentication.FirebaseDefaultAuthenticationService
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

class PersonRepository(
    private val personDAO: PersonDAO,
    private val userDAO: UserDAO,
    private val firebaseDefaultAuthenticationService: FirebaseDefaultAuthenticationService,
    private val personWebClient: PersonWebClient
) {
    @Transaction
    suspend fun savePerson(toPerson: TOPerson) = withContext(IO) {
        val user = toPerson.toUser!!.getUser()
        val person = toPerson.getPerson(user.id)
        val existentUser = userDAO.findById(user.id)

        if (existentUser == null) {
            firebaseDefaultAuthenticationService.register(user.email!!, user.password!!)
        } else {
            firebaseDefaultAuthenticationService.updateUserInfos(user)
        }

        if (toPerson.id == null) {
            userDAO.insert(user)
            personDAO.insert(person)

            toPerson.id = person.id
            toPerson.toUser?.id = user.id
        } else {
            userDAO.update(user)
            personDAO.update(person)
        }

        personWebClient.savePerson(person, user)
    }

    @Transaction
    suspend fun savePersonBatch(toPersons: List<TOPerson>) = withContext(IO) {
        val users = toPersons.map { it.toUser!!.getUser() }
        val persons = toPersons.map { it.getPerson(it.toUser?.id!!) }

        if (toPersons.first().id == null) {
            userDAO.insertBatch(users)
            personDAO.insertBatch(persons)
        } else {
            userDAO.updateBatch(users)
            personDAO.updateBatch(persons)
        }
    }

    suspend fun getTOPersonById(personId: String): TOPerson = withContext(IO) {
        personDAO.findPersonById(personId).getTOPerson()!!
    }

    suspend fun getAuthenticatedTOPerson(): TOPerson? = withContext(IO) {
        val toUser = userDAO.getAuthenticatedUser()?.getTOUser() ?: return@withContext null
        personDAO.findPersonByUserId(toUser.id!!).getTOPerson()
    }

    fun getListTOPersonWithUserType(
        types: List<EnumUserType>,
        simpleFilter: String,
        personsForSchedule: Boolean
    ): Pager<Int, PersonTuple> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                personDAO.getPersonsWithUserType(
                    types = types,
                    simpleFilter = simpleFilter,
                    personsForSchedule = personsForSchedule
                )
            }
        )
    }

    suspend fun findPersonById(personId: String): Person = withContext(IO) {
        personDAO.findPersonById(personId)
    }

    private suspend fun TOPerson.getPerson(userId: String): Person {
        return if (id == null) {
            Person(
                name = name,
                birthDate = birthDate,
                phone = phone,
                userId = userId,
                active = active
            )
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
            User(
                email = email,
                password = password,
                type = type,
                active = active
            )
        } else {
            userDAO.findById(id!!)!!.copy(
                email = email,
                password = password,
                type = type,
                active = active
            )
        }
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
                active = active,
                serviceToken = serviceToken
            )
        }
    }
}