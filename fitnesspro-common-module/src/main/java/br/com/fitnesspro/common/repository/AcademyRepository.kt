package br.com.fitnesspro.common.repository

import br.com.fitnesspro.common.R
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.common.ui.screen.registeruser.decorator.AcademyGroupDecorator
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class AcademyRepository(
    private val academyDAO: AcademyDAO
) {

    suspend fun savePersonAcademyTime(academyTime: PersonAcademyTime) = withContext(IO) {
        academyDAO.saveAcademyTime(academyTime)
    }

    suspend fun getAcademies(): List<Academy> = withContext(IO) {
        academyDAO.getAcademies()
    }

    suspend fun getAcademyById(id: String): Academy = withContext(IO) {
        academyDAO.findAcademyById(id)
    }

    suspend fun getConflictPersonAcademyTime(personAcademyTime: PersonAcademyTime): PersonAcademyTime? = withContext(IO) {
        academyDAO.getConflictPersonAcademyTime(
            personAcademyTimeId = personAcademyTime.id,
            personId = personAcademyTime.personId!!,
            dayOfWeek = personAcademyTime.dayOfWeek!!,
            start = personAcademyTime.timeStart!!,
            end = personAcademyTime.timeEnd!!
        )
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

    suspend fun getTOPersonAcademyTimeById(personAcademyTimeId: String): TOPersonAcademyTime = withContext(IO) {
        academyDAO.findPersonAcademyTimeById(personAcademyTimeId).getTOPersonAcademyTime()!!
    }

    suspend fun findPersonAcademyTimeById(personAcademyTimeId: String): PersonAcademyTime = withContext(IO) {
        academyDAO.findPersonAcademyTimeById(personAcademyTimeId)
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