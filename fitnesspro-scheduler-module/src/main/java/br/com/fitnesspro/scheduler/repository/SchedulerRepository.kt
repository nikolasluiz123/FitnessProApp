package br.com.fitnesspro.scheduler.repository

import androidx.room.Transaction
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
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
    private val schedulerDAO: SchedulerDAO,
    private val workoutDAO: WorkoutDAO,
    private val workoutGroupDAO: WorkoutGroupDAO,
    private val userRepository: UserRepository,
    private val personRepository: PersonRepository,
    private val schedulerWebClient: SchedulerWebClient
) {

    suspend fun saveScheduler(
        toScheduler: TOScheduler,
        schedulerType: EnumSchedulerType
    ) = withContext(IO) {
        val scheduler = toScheduler.getScheduler()

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
            schedulerDAO.update(scheduler)
        }
    }

    private suspend fun saveSchedulerRemote(
        scheduler: Scheduler,
        schedulerType: EnumSchedulerType
    ) {
        userRepository.getAuthenticatedTOUser()?.serviceToken?.let { token ->
            val response = schedulerWebClient.saveScheduler(
                token = token,
                scheduler = scheduler,
                schedulerType = schedulerType.name
            )

            if (response.success) {
                schedulerDAO.update(scheduler.copy(transmissionDate = response.transmissionDate))
            }
        }
    }

    suspend fun saveSchedulerBatch(toScheduler: List<TOScheduler>, schedulerType: EnumSchedulerType) = withContext(IO) {
        saveSchedulerBatchLocally(toScheduler)
        saveSchedulerBatchRemote(toScheduler, schedulerType)
    }

    private suspend fun saveSchedulerBatchLocally(toSchedulerList: List<TOScheduler>) {
        val insertionList = mutableListOf<Scheduler>()
        val updateList = mutableListOf<Scheduler>()

        toSchedulerList.forEach { toScheduler ->
            val scheduler = toScheduler.getScheduler()

            if (toScheduler.id == null) {
                insertionList.add(scheduler)
            } else {
                updateList.add(scheduler)
            }
        }

        if (insertionList.isNotEmpty()) {
            schedulerDAO.insertBatch(insertionList)
        }

        if (updateList.isNotEmpty()) {
            schedulerDAO.updateBatch(updateList)
        }
    }

    private suspend fun saveSchedulerBatchRemote(
        toScheduler: List<TOScheduler>,
        schedulerType: EnumSchedulerType
    ) {
        userRepository.getAuthenticatedTOUser()?.serviceToken?.let { token ->
            schedulerWebClient.saveSchedulerBatch(
                token = token,
                schedulerList = toScheduler.map { it.getScheduler() },
                schedulerType = schedulerType.name
            )
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
            userType = person.toUser?.type!!,
            yearMonth = yearMonth,
            scheduledDate = scheduledDate
        )
    }

    suspend fun getTOSchedulerById(id: String): TOScheduler = withContext(IO) {
        schedulerDAO.findSchedulerById(id).getTOScheduler()!!
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

    private suspend fun Scheduler?.getTOScheduler(): TOScheduler? {
        return this?.run {
            val memberPerson = personRepository.findPersonById(academyMemberPersonId!!)
            val professionalPerson = personRepository.findPersonById(professionalPersonId!!)
            val professionalUser = userRepository.findUserById(professionalPerson.userId!!)!!

            TOScheduler(
                id = id,
                academyMemberPersonId = academyMemberPersonId,
                academyMemberName = memberPerson.name,
                professionalPersonId = professionalPersonId,
                professionalName = professionalPerson.name,
                professionalType = professionalUser.type,
                scheduledDate = scheduledDate,
                start = start,
                end = end,
                canceledDate = canceledDate,
                situation = situation,
                compromiseType = compromiseType,
                observation = observation,
                active = active
            )
        }
    }

    private suspend fun TOScheduler.getScheduler(): Scheduler {
        return if (id == null) {
            Scheduler(
                academyMemberPersonId = academyMemberPersonId,
                professionalPersonId = professionalPersonId,
                scheduledDate = scheduledDate,
                start = start,
                end = end,
                canceledDate = canceledDate,
                situation = situation,
                compromiseType = compromiseType,
                observation = observation,
                active = active
            )
        } else {
            schedulerDAO.findSchedulerById(id!!).copy(
                academyMemberPersonId = academyMemberPersonId,
                professionalPersonId = professionalPersonId,
                scheduledDate = scheduledDate,
                start = start,
                end = end,
                canceledDate = canceledDate,
                situation = situation,
                compromiseType = compromiseType,
                observation = observation,
                active = active
            )
        }
    }

    @Transaction
    suspend fun saveRecurrentScheduler(schedules: List<TOScheduler>) = withContext(IO) {
        val schedulers = schedules.map { it.getScheduler() }.sortedBy { it.scheduledDate }

        val workout = Workout(
            academyMemberPersonId = schedules.first().academyMemberPersonId,
            professionalPersonId = schedules.first().professionalPersonId,
            start = schedulers.first().scheduledDate,
            end = schedulers.last().scheduledDate
        )

        val workoutGroups = schedulers.map { it.scheduledDate!!.dayOfWeek }.distinct().map {
            WorkoutGroup(dayWeek = it, workoutId = workout.id)
        }

        schedulerDAO.insertBatch(schedulers)
        workoutDAO.insert(workout)
        workoutGroupDAO.insertBatch(workoutGroups)

        userRepository.getAuthenticatedTOUser()!!.also { user ->
            schedulerWebClient.saveScheduler(
                token = user.serviceToken!!,
                scheduler = schedulers.first(),
                schedulerType = EnumSchedulerType.RECURRENT.name,
                dateStart = workout.start,
                dateEnd = workout.end,
                dayWeeks = workoutGroups.map { it.dayWeek!! }
            )
        }
    }
}