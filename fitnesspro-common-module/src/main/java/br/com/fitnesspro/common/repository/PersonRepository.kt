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

        saveUserOnFirebase(user)
        savePersonLocally(toPerson, user, person)
        savePersonRemote(person, user)
    }

    private suspend fun saveUserOnFirebase(user: User) {
        val existentUser = userDAO.findById(user.id)

        if (existentUser == null) {
            firebaseDefaultAuthenticationService.register(user.email!!, user.password!!)
        } else {
            firebaseDefaultAuthenticationService.updateUserInfos(user)
        }
    }

    private suspend fun savePersonLocally(toPerson: TOPerson, user: User, person: Person) {
        if (toPerson.id == null) {
            userDAO.insert(user)
            personDAO.insert(person)

            toPerson.id = person.id
            toPerson.toUser?.id = user.id
        } else {
            userDAO.update(user)
            personDAO.update(person)
        }
    }

    private suspend fun savePersonRemote(person: Person, user: User) {
        val response = personWebClient.savePerson(person, user)

        if (response.success) {
            userDAO.update(user.copy(transmissionDate = response.transmissionDate))
            personDAO.update(person.copy(transmissionDate = response.transmissionDate))
        }
    }

    @Transaction
    suspend fun savePersonBatch(toPersons: List<TOPerson>) = withContext(IO) {
        savePersonBatchLocally(toPersons)
        savePersonBatchRemote(toPersons)
    }

    private suspend fun savePersonBatchLocally(toPersons: List<TOPerson>) {
        val insertionUserList = mutableListOf<User>()
        val insertionPersonList = mutableListOf<Person>()

        val updateUserList = mutableListOf<User>()
        val updatePersonList = mutableListOf<Person>()

        toPersons.forEach { toPerson ->
            val user = toPerson.toUser!!.getUser()
            val person = toPerson.getPerson(user.id)

            if (toPerson.id == null) {
                insertionUserList.add(user)
                insertionPersonList.add(person)
            } else {
                updateUserList.add(user)
                updatePersonList.add(person)
            }
        }

        if (insertionUserList.isNotEmpty()) {
            userDAO.insertBatch(insertionUserList)
        }

        if (updateUserList.isNotEmpty()) {
            userDAO.updateBatch(updateUserList)
        }

        if (insertionPersonList.isNotEmpty()) {
            personDAO.insertBatch(insertionPersonList)
        }

        if (updatePersonList.isNotEmpty()) {
            personDAO.updateBatch(updatePersonList)
        }
    }

    private suspend fun savePersonBatchRemote(toPersons: List<TOPerson>) {
        userDAO.getAuthenticatedUser()?.serviceToken?.let { token ->
            val users = mutableListOf<User>()
            val persons = mutableListOf<Person>()

            toPersons.forEach { toPerson ->
                val user = toPerson.toUser!!.getUser()
                val person = toPerson.getPerson(user.id)

                users.add(user)
                persons.add(person)
            }

            val response = personWebClient.savePersonBatch(
                token = token,
                persons = persons,
                users = users
            )

            if (response.success) {
                val transmittedUsers = users.map { it.copy(transmissionDate = response.transmissionDate) }
                val transmittedPersons = persons.map { it.copy(transmissionDate = response.transmissionDate) }

                userDAO.updateBatch(transmittedUsers)
                personDAO.updateBatch(transmittedPersons)
            }
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