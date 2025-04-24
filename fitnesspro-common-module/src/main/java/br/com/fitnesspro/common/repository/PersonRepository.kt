package br.com.fitnesspro.common.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.firebase.api.authentication.FirebaseDefaultAuthenticationService
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.mappers.getPerson
import br.com.fitnesspro.mappers.getTOPerson
import br.com.fitnesspro.mappers.getUser
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.tuple.PersonTuple
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext


class PersonRepository(
    context: Context,
    private val personDAO: PersonDAO,
    private val userDAO: UserDAO,
    private val firebaseDefaultAuthenticationService: FirebaseDefaultAuthenticationService,
    private val personWebClient: PersonWebClient,
): FitnessProRepository(context) {

    suspend fun savePerson(
        toPerson: TOPerson,
        isRegisterServiceAuth: Boolean,
        forceInsertLocally: Boolean = false
    ) = withContext(IO) {
        val user = toPerson.user!!.getUser()
        val person = toPerson.getPerson()

        savePersonLocally(toPerson, user, person, forceInsertLocally)

        val successRemoteSave = savePersonRemote(person, user)

        if (successRemoteSave) {
            saveUserOnFirebase(user, isRegisterServiceAuth)
        }
    }

    private suspend fun saveUserOnFirebase(user: User, isRegisterServiceAuth: Boolean) {
        val isAuthenticated = getAuthenticatedUser() != null

        if (isAuthenticated || isRegisterServiceAuth) {
            firebaseDefaultAuthenticationService.updateUserInfos(user)
        } else {
            firebaseDefaultAuthenticationService.register(user.email!!, user.password!!)
        }
    }

    private suspend fun savePersonLocally(
        toPerson: TOPerson,
        user: User,
        person: Person,
        forceInsert: Boolean
    ) {
        if (toPerson.id == null || forceInsert) {
            userDAO.insert(user)
            personDAO.insert(person)

            toPerson.id = person.id
            toPerson.user?.id = user.id
        } else {
            userDAO.update(user, true)
            personDAO.update(person,true)
        }
    }

    suspend fun savePersonRemote(person: Person, user: User): Boolean {
        val response = personWebClient.savePerson(
            token = getValidToken(withoutAuthentication = true),
            person = person,
            user = user
        )

        if (response.success) {
            userDAO.update(
                model = user.copy(transmissionState = EnumTransmissionState.TRANSMITTED),
                writeTransmissionState = true
            )
            personDAO.update(
                model = person.copy(transmissionState = EnumTransmissionState.TRANSMITTED),
                writeTransmissionState = true
            )
        }

        return response.success
    }

    suspend fun savePersonBatch(toPersons: List<TOPerson>) = withContext(IO) {
        runInTransaction {
            savePersonBatchLocally(toPersons)
            savePersonBatchRemote(toPersons)
        }
    }

    private suspend fun savePersonBatchLocally(toPersons: List<TOPerson>) {
        val insertionUserList = mutableListOf<User>()
        val insertionPersonList = mutableListOf<Person>()

        val updateUserList = mutableListOf<User>()
        val updatePersonList = mutableListOf<Person>()

        toPersons.forEach { toPerson ->
            val user = toPerson.user!!.getUser()
            val person = toPerson.getPerson()

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
        val users = mutableListOf<User>()
        val persons = mutableListOf<Person>()

        toPersons.forEach { toPerson ->
            val user = toPerson.user?.getUser()!!
            val person = toPerson.getPerson()

            users.add(user)
            persons.add(person)
        }

        val response = personWebClient.savePersonBatch(
            token = getValidToken(withoutAuthentication = true),
            persons = persons,
            users = users
        )

        if (response.success) {
            val transmittedUsers = users.map { it.copy(transmissionState = EnumTransmissionState.TRANSMITTED) }
            val transmittedPersons = persons.map { it.copy(transmissionState = EnumTransmissionState.TRANSMITTED) }

            userDAO.updateBatch(transmittedUsers)
            personDAO.updateBatch(transmittedPersons)
        }
    }

    suspend fun getTOPersonById(personId: String): TOPerson = withContext(IO) {
        val person = personDAO.findPersonById(personId)
        val user = userDAO.findById(person.userId!!)

        person.getTOPerson(user!!)
    }

    suspend fun getAuthenticatedTOPerson(): TOPerson? = withContext(IO) {
        getAuthenticatedUser()?.let { user ->
            personDAO.findPersonByUserId(user.id).getTOPerson(user)
        }
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

    suspend fun findPersonByUserId(userId: String): Person = withContext(IO) {
        personDAO.findPersonByUserId(userId)
    }

    suspend fun findPersonByEmailRemote(email: String): TOPerson? = withContext(IO) {
        if (context.isNetworkAvailable()) {
            val response =  personWebClient.findPersonByEmail(
                token = getValidToken(withoutAuthentication = true),
                email = email
            )

            if (response.success) response.value?.getTOPerson() else null
        } else {
            null
        }
    }
}