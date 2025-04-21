package br.com.fitnesspro.scheduler.repository

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.mappers.SchedulerModelMapper
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOScheduler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

class SchedulerRepository(
    context: Context,
    private val schedulerDAO: SchedulerDAO,
    private val workoutDAO: WorkoutDAO,
    private val workoutGroupDAO: WorkoutGroupDAO,
    private val userRepository: UserRepository,
    private val personRepository: PersonRepository,
    private val schedulerWebClient: SchedulerWebClient,
    private val schedulerModelMapper: SchedulerModelMapper
): FitnessProRepository(context) {

    suspend fun saveScheduler(
        toScheduler: TOScheduler,
        schedulerType: EnumSchedulerType
    ) = withContext(IO) {
        val scheduler = schedulerModelMapper.getScheduler(toScheduler)

        saveSchedulerLocally(toScheduler, scheduler)
        saveSchedulerRemote(scheduler, schedulerType)
    }

    private suspend fun saveSchedulerLocally(
        toScheduler: TOScheduler,
        scheduler: Scheduler
    ) {
        if (toScheduler.id == null) {
            schedulerDAO.insert(scheduler)
            toScheduler.id = scheduler.id
        } else {
            schedulerDAO.update(scheduler, true)
        }
    }

    private suspend fun saveSchedulerRemote(
        scheduler: Scheduler,
        schedulerType: EnumSchedulerType
    ) {
        val response = schedulerWebClient.saveScheduler(
            token = getValidToken(),
            scheduler = scheduler,
            schedulerType = schedulerType.name
        )

        if (response.success) {
            schedulerDAO.update(scheduler.copy(transmissionState = EnumTransmissionState.TRANSMITTED))
        }
    }

    suspend fun getSchedulerList(
        yearMonth: YearMonth? = null,
        toPerson: TOPerson? = null,
        scheduledDate: LocalDate? = null
    ): List<TOScheduler> = withContext(IO) {
        val person = toPerson ?: personRepository.getAuthenticatedTOPerson()!!

        schedulerDAO.getSchedulerList(
            personId = person.id!!,
            userType = person.user?.type!!,
            yearMonth = yearMonth,
            scheduledDate = scheduledDate
        )
    }

    suspend fun getTOSchedulerById(id: String): TOScheduler = withContext(IO) {
        val scheduler = schedulerDAO.findSchedulerById(id)

        val memberPerson = personRepository.findPersonById(scheduler.academyMemberPersonId!!)
        val professionalPerson = personRepository.findPersonById(scheduler.professionalPersonId!!)
        val professionalUser = userRepository.findUserById(professionalPerson.userId!!)!!

        schedulerModelMapper.getTOScheduler(
            scheduler = scheduler,
            memberPersonName = memberPerson.name!!,
            professionalPersonName = professionalPerson.name!!,
            professionalUserType = professionalUser.type!!
        )
    }

    suspend fun getHasSchedulerConflict(
        schedulerId: String?,
        personId: String,
        userType: EnumUserType,
        scheduledDate: LocalDate,
        start: LocalTime,
        end: LocalTime
    ): Boolean = withContext(IO) {
        schedulerDAO.getHasSchedulerConflict(
            schedulerId = schedulerId,
            personId = personId,
            userType = userType,
            scheduledDate = scheduledDate,
            start = start,
            end = end
        )
    }


    suspend fun saveRecurrentScheduler(schedules: List<TOScheduler>) = withContext(IO) {
        val schedulers = schedules.map(schedulerModelMapper::getScheduler).sortedBy(Scheduler::scheduledDate)

        val workout = Workout(
            academyMemberPersonId = schedules.first().academyMemberPersonId,
            professionalPersonId = schedules.first().professionalPersonId,
            dateStart = schedulers.first().scheduledDate,
            dateEnd = schedulers.last().scheduledDate
        )

        val workoutGroups = schedulers.map { it.scheduledDate!!.dayOfWeek }.distinct().map {
            WorkoutGroup(dayWeek = it, workoutId = workout.id)
        }

        runInTransaction {
            schedulerDAO.insertBatch(schedulers)
            workoutDAO.insert(workout)
            workoutGroupDAO.insertBatch(workoutGroups)
        }

        schedulerWebClient.saveScheduler(
            token = getValidToken(),
            scheduler = schedulers.first(),
            schedulerType = EnumSchedulerType.RECURRENT.name,
            dateStart = workout.dateStart,
            dateEnd = workout.dateEnd,
            dayWeeks = workoutGroups.map { it.dayWeek!! }
        )
    }
}