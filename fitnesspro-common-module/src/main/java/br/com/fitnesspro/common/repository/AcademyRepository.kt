package br.com.fitnesspro.common.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.common.ui.screen.registeruser.decorator.AcademyGroupDecorator
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.local.data.access.dao.PersonAcademyTimeDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.tuple.AcademyTuple
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.time.DayOfWeek

class AcademyRepository(
    context: Context,
    private val academyDAO: AcademyDAO,
    private val personAcademyTimeDAO: PersonAcademyTimeDAO,
    private val userDAO: UserDAO,
    private val personWebClient: PersonWebClient
): FitnessProRepository(context) {

    suspend fun savePersonAcademyTime(toPersonAcademyTime: TOPersonAcademyTime) = withContext(IO) {
        val personAcademyTime = toPersonAcademyTime.getPersonAcademyTime()

        savePersonAcademyTimeLocally(toPersonAcademyTime, personAcademyTime)
        savePersonAcademyTimeRemote(personAcademyTime)
    }

    private suspend fun savePersonAcademyTimeLocally(
        toPersonAcademyTime: TOPersonAcademyTime,
        personAcademyTime: PersonAcademyTime
    ) {
        val userId = getAuthenticatedUser()?.id!!

        if (toPersonAcademyTime.id == null) {
            personAcademyTimeDAO.insert(personAcademyTime, userId, true)
            toPersonAcademyTime.id = personAcademyTime.id
        } else {
            personAcademyTimeDAO.update(personAcademyTime, userId, true)
        }
    }

    private suspend fun savePersonAcademyTimeRemote(personAcademyTime: PersonAcademyTime) {
        getAuthenticatedUser()?.serviceToken?.let { token ->
            val response = personWebClient.savePersonAcademyTime(
                token = token,
                personAcademyTime = personAcademyTime
            )

            if (response.success) {
                personAcademyTimeDAO.update(personAcademyTime.copy(transmissionDate = response.transmissionDate))
            }
        }
    }

    fun getAcademies(simpleFilter: String): Pager<Int, AcademyTuple> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                academyDAO.getAcademies(name = simpleFilter)
            }
        )
    }

    suspend fun getConflictPersonAcademyTime(toPersonAcademyTime: TOPersonAcademyTime): PersonAcademyTime? = withContext(IO) {
        personAcademyTimeDAO.getConflictPersonAcademyTime(
            personAcademyTimeId = toPersonAcademyTime.id,
            personId = toPersonAcademyTime.personId!!,
            dayOfWeek = toPersonAcademyTime.dayOfWeek!!,
            start = toPersonAcademyTime.timeStart!!,
            end = toPersonAcademyTime.timeEnd!!
        )
    }

    suspend fun getAcademiesFromPerson(personId: String): List<AcademyGroupDecorator> = withContext(IO) {
        val toAcademyList = academyDAO.getAcademiesFromPerson(personId = personId).map { it.getTOAcademy()!! }

        val personAcademyTimes = toAcademyList.flatMap { academy ->
            personAcademyTimeDAO.getAcademyTimes(personId = personId, academyId = academy.id!!)
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

    suspend fun getAcademyTimes(
        personId: String,
        academyId: String? = null,
        dayOfWeek: DayOfWeek? = null
    ): List<PersonAcademyTime> = withContext(IO) {
        personAcademyTimeDAO.getAcademyTimes(
            personId = personId,
            academyId = academyId,
            dayOfWeek = dayOfWeek
        )
    }

    suspend fun getTOPersonAcademyTimeById(personAcademyTimeId: String): TOPersonAcademyTime = withContext(IO) {
        personAcademyTimeDAO.findPersonAcademyTimeById(personAcademyTimeId).getTOPersonAcademyTime()!!
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

    private suspend fun TOPersonAcademyTime.getPersonAcademyTime(): PersonAcademyTime {
        return if (id != null) {
            personAcademyTimeDAO.findPersonAcademyTimeById(id!!).copy(
                personId = personId,
                academyId = toAcademy?.id,
                timeStart = timeStart,
                timeEnd = timeEnd,
                dayOfWeek = dayOfWeek,
                active = active
            )
        } else {
            PersonAcademyTime(
                personId = personId,
                academyId = toAcademy?.id,
                timeStart = timeStart,
                timeEnd = timeEnd,
                dayOfWeek = dayOfWeek,
                active = active
            )
        }
    }

    suspend fun inactivatePersonAcademyTime(toPersonAcademyTime: TOPersonAcademyTime) = withContext(IO) {
        toPersonAcademyTime.active = false
        savePersonAcademyTime(toPersonAcademyTime)
    }

    suspend fun savePersonAcademyTimeBatch(toPersonAcademyTimeList: List<TOPersonAcademyTime>) = withContext(IO) {
        savePersonAcademyTimeBatchLocally(toPersonAcademyTimeList)
        savePersonAcademyTimeBatchRemote(toPersonAcademyTimeList)
    }

    private suspend fun savePersonAcademyTimeBatchLocally(toPersonAcademyTimeList: List<TOPersonAcademyTime>) {
        val insertionList = mutableListOf<PersonAcademyTime>()
        val updateList = mutableListOf<PersonAcademyTime>()

        toPersonAcademyTimeList.forEach { toPersonAcademyTime ->
            if (toPersonAcademyTime.id == null) {
                insertionList.add(toPersonAcademyTime.getPersonAcademyTime())
            } else {
                updateList.add(toPersonAcademyTime.getPersonAcademyTime())
            }
        }

        if (insertionList.isNotEmpty()) {
            personAcademyTimeDAO.insertBatch(insertionList)
        }

        if (updateList.isNotEmpty()) {
            personAcademyTimeDAO.updateBatch(updateList)
        }
    }

    private suspend fun savePersonAcademyTimeBatchRemote(toPersonAcademyTimeList: List<TOPersonAcademyTime>) {
        getAuthenticatedUser()?.serviceToken?.let { token ->
            val personAcademyTimeList = toPersonAcademyTimeList.map { it.getPersonAcademyTime() }

            val response = personWebClient.savePersonAcademyTimeBatch(
                token = token,
                personAcademyTimeList = personAcademyTimeList
            )

            if (response.success) {
                personAcademyTimeDAO.updateBatch(
                    models = personAcademyTimeList.map { 
                        it.copy(transmissionDate = response.transmissionDate)
                    }
                )
            }
        }
    }
}