package br.com.fitnesspro.common.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.screen.registeruser.decorator.AcademyGroupDecorator
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.tuple.AcademyTuple
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.time.DayOfWeek

class AcademyRepository(
    private val academyDAO: AcademyDAO
) {

    suspend fun savePersonAcademyTime(toPersonAcademyTime: TOPersonAcademyTime) = withContext(IO) {
        academyDAO.saveAcademyTime(toPersonAcademyTime.getPersonAcademyTime())
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
        academyDAO.getConflictPersonAcademyTime(
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

    suspend fun getAcademyTimes(
        personId: String,
        academyId: String? = null,
        dayOfWeek: DayOfWeek? = null
    ): List<PersonAcademyTime> = withContext(IO) {
        academyDAO.getAcademyTimes(
            personId = personId,
            academyId = academyId,
            dayOfWeek = dayOfWeek
        )
    }

    suspend fun getTOPersonAcademyTimeById(personAcademyTimeId: String): TOPersonAcademyTime = withContext(IO) {
        academyDAO.findPersonAcademyTimeById(personAcademyTimeId).getTOPersonAcademyTime()!!
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
            academyDAO.findPersonAcademyTimeById(id!!).copy(
                personId = personId,
                academyId = toAcademy?.id,
                timeStart = timeStart,
                timeEnd = timeEnd,
                dayOfWeek = dayOfWeek,
                active = active
            )
        } else {
            val model = PersonAcademyTime(
                personId = personId,
                academyId = toAcademy?.id,
                timeStart = timeStart,
                timeEnd = timeEnd,
                dayOfWeek = dayOfWeek,
                active = active
            )

            this.id = model.id

            model
        }
    }

    suspend fun inactivatePersonAcademyTime(toPersonAcademyTime: TOPersonAcademyTime) = withContext(IO) {
        toPersonAcademyTime.active = false
        savePersonAcademyTime(toPersonAcademyTime)
    }

    suspend fun savePersonAcademyTimeBatch(toPersonAcademyTimes: List<TOPersonAcademyTime>) = withContext(IO) {
        val times = toPersonAcademyTimes.map { it.getPersonAcademyTime() }
        academyDAO.savePersonAcademyTimesBatch(times)
    }
}