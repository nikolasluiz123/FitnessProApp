package br.com.fitnesspro.common.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import br.com.fitnesspro.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspro.common.repository.common.FitnessProRepository
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
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.responses.SingleValueServiceResponse
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.tuple.PersonTuple
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate


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
    ) {
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
        val findPersonResponse = findPersonByEmailRemote(user.email!!, user.password!!)

        if (isAuthenticated || isRegisterServiceAuth || findPersonResponse.value != null) {
            firebaseDefaultAuthenticationService.updateUserInfos(context, user)
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
        schedulerDate: LocalDate?,
        personsForSchedule: Boolean,
        authenticatedPersonId: String
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
                    schedulerDate = schedulerDate,
                    personsForSchedule = personsForSchedule,
                    authenticatedPersonId = authenticatedPersonId
                )
            }
        )
    }

    suspend fun findPersonById(personId: String): Person {
        return personDAO.findPersonById(personId)
    }

    suspend fun findPersonByUserId(userId: String): Person {
        return personDAO.findPersonByUserId(userId)
    }

    suspend fun findPersonByEmailRemote(email: String, password: String?): SingleValueServiceResponse<PersonDTO?> {
        return personWebClient.findPersonByEmail(
            token = getValidToken(withoutAuthentication = true),
            email = email,
            password = password
        )
    }

    suspend fun getPersonMembersFromPersonalTrainer(simpleFilter: String = ""): Flow<List<PersonTuple>> = withContext(IO) {
        personDAO.getPersonMembersFromPersonalTrainer(
            simpleFilter = simpleFilter,
            authenticatedPersonId = getAuthenticatedTOPerson()?.id!!
        )
    }
}