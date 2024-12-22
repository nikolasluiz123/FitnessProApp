package br.com.fitnesspro.repository

import br.com.fitnesspro.R
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.to.TOUser
import br.com.fitnesspro.ui.screen.registeruser.decorator.AcademyGroupDecorator
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

    suspend fun getAcademies(personId: String): List<AcademyGroupDecorator> = withContext(IO) {
        val academies = personDAO.getAcademies(personId = personId)
        val personAcademyTimes = academies.flatMap { academy ->
            personDAO.getAcademyTimes(personId = personId, academyId = academy.id)
        }

        val groups = academies.map { academy ->
            val academyTimes = personAcademyTimes.filter { it.academyId == academy.id }
            val items = academyTimes.map {
                TOPersonAcademyTime(
                    id = it.id,
                    personId = it.personId,
                    toAcademy = TOAcademy(
                        id = it.academyId,
                        name = academy.name,
                        address = academy.address,
                        phone = academy.phone,
                    ),
                    timeStart = it.timeStart,
                    timeEnd = it.timeEnd,
                    dayOfWeek = it.dayOfWeek,
                )
            }

            AcademyGroupDecorator(
                label = R.string.label_academy_group,
                value = academy.name!!,
                isExpanded = false,
                items = items
            )
        }

        groups
    }

}