package br.com.fitnesspro.repository

import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.to.TOScheduler
import java.time.YearMonth

class SchedulerRepository(
    private val schedulerDAO: SchedulerDAO,
    private val userRepository: UserRepository
) {

    suspend fun getSchedulerList(yearMonth: YearMonth): List<TOScheduler> {
        val toPerson = userRepository.getAuthenticatedTOPerson()!!

        return schedulerDAO.getSchedulerList(
            personId = toPerson.id!!,
            userType = toPerson.toUser?.type!!,
            yearMonth = yearMonth
        )
    }

}