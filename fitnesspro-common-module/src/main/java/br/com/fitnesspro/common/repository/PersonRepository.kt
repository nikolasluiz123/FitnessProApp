package br.com.fitnesspro.common.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Transaction
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.firebase.api.authentication.FirebaseDefaultAuthenticationService
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.mappers.toPerson
import br.com.fitnesspro.mappers.toTOPerson
import br.com.fitnesspro.mappers.toTOUser
import br.com.fitnesspro.mappers.toUser
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.UserDTO
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser
import br.com.fitnesspro.tuple.PersonTuple
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import br.com.fitnesspro.models.general.enums.EnumUserType as EnumUserTypeService

class PersonRepository(
    context: Context,
    private val personDAO: PersonDAO,
    private val userDAO: UserDAO,
    private val firebaseDefaultAuthenticationService: FirebaseDefaultAuthenticationService,
    private val personWebClient: PersonWebClient
): FitnessProRepository(context) {

    suspend fun savePerson(
        toPerson: TOPerson,
        isRegisterServiceAuth: Boolean,
        forceInsertLocally: Boolean = false
    ) = withContext(IO) {
        val user = toPerson.toUser!!.toUser()
        val person = toPerson.toPerson(user.id)

        saveUserOnFirebase(user, isRegisterServiceAuth)
        savePersonLocally(toPerson, user, person, forceInsertLocally)
        savePersonRemote(person, user)
    }

    private suspend fun saveUserOnFirebase(user: User, isRegisterServiceAuth: Boolean) {
        val existentUser = userDAO.findById(user.id)

        if (existentUser == null && !isRegisterServiceAuth) {
            firebaseDefaultAuthenticationService.register(user.email!!, user.password!!)
        } else {
            firebaseDefaultAuthenticationService.updateUserInfos(user)
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
            toPerson.toUser?.id = user.id
        } else {
            userDAO.update(user, true)
            personDAO.update(person,true)
        }
    }

    suspend fun savePersonRemote(person: Person, user: User) {
        val response = personWebClient.savePerson(person, user)

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
            val user = toPerson.toUser!!.toUser()
            val person = toPerson.toPerson(user.id)

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
        getAuthenticatedUser()?.serviceToken?.let { token ->
            val users = mutableListOf<User>()
            val persons = mutableListOf<Person>()

            toPersons.forEach { toPerson ->
                val user = toPerson.toUser!!.toUser()
                val person = toPerson.toPerson(user.id)

                users.add(user)
                persons.add(person)
            }

            val response = personWebClient.savePersonBatch(
                token = token,
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
    }

    suspend fun getTOPersonById(personId: String): TOPerson = withContext(IO) {
        val toUser = getAuthenticatedUser()?.toTOUser()!!
        personDAO.findPersonById(personId).toTOPerson(toUser)
    }

    suspend fun getAuthenticatedTOPerson(): TOPerson? = withContext(IO) {
        val toUser = getAuthenticatedUser()?.toTOUser() ?: return@withContext null
        personDAO.findPersonByUserId(toUser.id!!).toTOPerson(toUser)
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
            val response =  personWebClient.findPersonByEmail(email)
            if (response.success) response.value?.toTOPerson() else null
        } else {
            null
        }
    }

    private fun PersonDTO.toTOPerson(): TOPerson {
        return TOPerson(
            id = id,
            name = name,
            active = active,
            birthDate = birthDate,
            phone = phone,
            toUser = user?.toTOUser()
        )
    }

    private fun UserDTO.toTOUser(): TOUser {
        return TOUser(
            id = id,
            email = email,
            password = password,
            active = active,
            type = getUserType(type!!),
        )
    }

    private fun getUserType(type: EnumUserTypeService): EnumUserType {
        return when (type) {
            EnumUserTypeService.PERSONAL_TRAINER -> EnumUserType.PERSONAL_TRAINER
            EnumUserTypeService.NUTRITIONIST -> EnumUserType.NUTRITIONIST
            EnumUserTypeService.ACADEMY_MEMBER -> EnumUserType.ACADEMY_MEMBER
        }
    }
}