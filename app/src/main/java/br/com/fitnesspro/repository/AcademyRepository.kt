package br.com.fitnesspro.repository

import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.PersonAcademyTime
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
        academyDAO.getAcademyById(id)
    }

    suspend fun getConflictPersonAcademyTime(personAcademyTime: PersonAcademyTime): PersonAcademyTime? = withContext(IO) {
        academyDAO.getConflictPersonAcademyTime(
            personId = personAcademyTime.personId!!,
            academyId = personAcademyTime.academyId!!,
            dayOfWeek = personAcademyTime.dayOfWeek!!,
            start = personAcademyTime.timeStart!!,
            end = personAcademyTime.timeEnd!!
        )
    }

}