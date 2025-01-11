package br.com.fitnesspro.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Transaction
import br.com.fitnesspro.R
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.to.TOUser
import br.com.fitnesspro.tuple.PersonTuple
import br.com.fitnesspro.ui.screen.registeruser.decorator.AcademyGroupDecorator
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class UserRepository(
    private val personDAO: PersonDAO,
    private val userDAO: UserDAO,
    private val academyDAO: AcademyDAO
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

    suspend fun getTOPersonAcademyTimeById(personAcademyTimeId: String): TOPersonAcademyTime = withContext(IO) {
        academyDAO.findPersonAcademyTimeById(personAcademyTimeId).getTOPersonAcademyTime()!!
    }

    suspend fun findPersonAcademyTimeById(personAcademyTimeId: String): PersonAcademyTime = withContext(IO) {
        academyDAO.findPersonAcademyTimeById(personAcademyTimeId)
    }

    suspend fun getAuthenticatedTOPerson(): TOPerson? = withContext(IO) {
        val toUser = userDAO.getAuthenticatedUser()?.getTOUser() ?: return@withContext null
        personDAO.findPersonByUserId(toUser.id!!).getTOPerson()
    }

    suspend fun getAuthenticatedTOUser(): TOUser? = withContext(IO) {
        userDAO.getAuthenticatedUser()?.getTOUser()
    }

    suspend fun getAcademies(personId: String): List<AcademyGroupDecorator> = withContext(IO) {
        val toAcademyList = academyDAO.getAcademies(personId = personId).map { it.getTOAcademy()!! }

        val personAcademyTimes = toAcademyList.flatMap { academy ->
            academyDAO.getAcademyTimes(personId = personId, academyId = academy.id!!)
        }

        val groups = toAcademyList.map { toAcademy ->
            val academyTimes = personAcademyTimes.filter { it.academyId == toAcademy.id }
            val items = academyTimes.map { it.getTOPersonAcademyTime()!! }.sortedBy { it.dayOfWeek!!.ordinal }

            AcademyGroupDecorator(
                id = toAcademy.id!!,
                label = R.string.label_academy_group,
                value = toAcademy.name!!,
                isExpanded = false,
                items = items
            )
        }

        groups
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

    private fun Academy?.getTOAcademy(): TOAcademy? {
        return this?.run {
            TOAcademy(
                id = id,
                name = name,
                address = address,
                phone = phone,
                active = active
            )
        }
    }

    private suspend fun PersonAcademyTime?.getTOPersonAcademyTime(): TOPersonAcademyTime? {
        return this?.run {
            TOPersonAcademyTime(
                id = id,
                personId = personId,
                toAcademy = academyDAO.findAcademyById(academyId!!).getTOAcademy(),
                timeStart = timeStart,
                timeEnd = timeEnd,
                dayOfWeek = dayOfWeek,
                active = active
            )
        }
    }

}