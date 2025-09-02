package br.com.fitnesspro.scheduler.repository

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.mappers.getScheduler
import br.com.fitnesspro.mappers.getTOScheduler
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
import java.time.OffsetDateTime
import java.time.YearMonth

class SchedulerRepository(
    context: Context,
    private val schedulerDAO: SchedulerDAO,
    private val workoutDAO: WorkoutDAO,
    private val workoutGroupDAO: WorkoutGroupDAO,
    private val userRepository: UserRepository,
    private val personRepository: PersonRepository,
    private val schedulerWebClient: SchedulerWebClient,
): FitnessProRepository(context) {

    suspend fun saveScheduler(
        toScheduler: TOScheduler,
        schedulerType: EnumSchedulerType
    ) {
        runInTransaction {
            val scheduler = toScheduler.getScheduler()

            saveSchedulerLocally(toScheduler, scheduler)
            saveSchedulerRemote(scheduler, schedulerType)
        }
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
        scheduledDate: LocalDate? = null,
        canceledSchedules: Boolean = true
    ): List<TOScheduler> = withContext(IO) {
        val person = toPerson ?: personRepository.getAuthenticatedTOPerson()!!

        schedulerDAO.getSchedulerList(
            personId = person.id!!,
            userType = person.user?.type!!,
            yearMonth = yearMonth,
            scheduledDate = scheduledDate,
            canceledSchedules = canceledSchedules
        )
    }

    suspend fun getTOSchedulerById(id: String): TOScheduler = withContext(IO) {
        val scheduler = schedulerDAO.findSchedulerById(id)

        val memberPerson = personRepository.findPersonById(scheduler?.academyMemberPersonId!!)
        val professionalPerson = personRepository.findPersonById(scheduler.professionalPersonId!!)
        val cancellationPerson = scheduler.cancellationPersonId?.let { personRepository.findPersonById(it) }
        val professionalUser = userRepository.findUserById(professionalPerson.userId!!)!!

        scheduler.getTOScheduler(
            memberPersonName = memberPerson.name!!,
            professionalPersonName = professionalPerson.name!!,
            cancellationPersonName = cancellationPerson?.name,
            professionalUserType = professionalUser.type!!
        )
    }

    suspend fun getHasSchedulerConflict(
        schedulerId: String?,
        personId: String,
        userType: EnumUserType,
        start: OffsetDateTime,
        end: OffsetDateTime
    ): Boolean {
        return schedulerDAO.getHasSchedulerConflict(
            schedulerId = schedulerId,
            personId = personId,
            userType = userType,
            start = start,
            end = end
        )
    }

    suspend fun saveRecurrentScheduler(schedules: List<TOScheduler>) {
        val schedulers = schedules.map { it.getScheduler() }.sortedBy {
            it.dateTimeStart?.toLocalDate()!!
        }

        val workout = Workout(
            academyMemberPersonId = schedules.first().academyMemberPersonId,
            professionalPersonId = schedules.first().professionalPersonId,
            dateStart = schedulers.first().dateTimeStart?.toLocalDate()!!,
            dateEnd = schedulers.last().dateTimeStart?.toLocalDate()!!
        )

        val workoutGroups = schedulers.map { it.dateTimeStart?.toLocalDate()!!.dayOfWeek }.distinct().map {
            WorkoutGroup(dayWeek = it, workoutId = workout.id)
        }

        runInTransaction {
            schedulerDAO.insertBatch(schedulers)
            workoutDAO.insert(workout)
            workoutGroupDAO.insertBatch(workoutGroups)

            val response = schedulerWebClient.saveRecurrentScheduler(
                token = getValidToken(),
                schedules = schedulers,
                workout = workout,
                workoutGroups = workoutGroups
            )

            if (response.success) {
                val transmittedSchedulers = schedulers.map {
                    it.copy(transmissionState = EnumTransmissionState.TRANSMITTED)
                }
                val transmittedWorkout = workout.copy(transmissionState = EnumTransmissionState.TRANSMITTED)
                val transmittedWorkoutGroups = workoutGroups.map {
                    it.copy(transmissionState = EnumTransmissionState.TRANSMITTED)
                }

                schedulerDAO.updateBatch(transmittedSchedulers)
                workoutDAO.update(transmittedWorkout)
                workoutGroupDAO.updateBatch(transmittedWorkoutGroups)
            }
        }
    }

    suspend fun hasSchedulerWithId(id: String?): Boolean = withContext(IO) {
        schedulerDAO.hasEntityWithId(id)
    }
}