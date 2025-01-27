package br.com.fitnesspro.scheduler.repository

import androidx.room.Transaction
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
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
    private val userRepository: UserRepository
) {

    suspend fun saveScheduler(toScheduler: TOScheduler) = withContext(IO) {
        val scheduler = toScheduler.getScheduler()
        schedulerDAO.save(scheduler)
    }

    suspend fun getSchedulerList(
        yearMonth: YearMonth? = null,
        toPerson: TOPerson? = null,
        scheduledDate: LocalDate? = null
    ): List<TOScheduler> = withContext(IO) {
        val person = toPerson ?: userRepository.getAuthenticatedTOPerson()!!

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

    suspend fun findSchedulerById(id: String): Scheduler = withContext(IO) {
        schedulerDAO.findSchedulerById(id)
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
            val memberPerson = userRepository.findPersonById(academyMemberPersonId!!)
            val professionalPerson = userRepository.findPersonById(professionalPersonId!!)
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
            val model = Scheduler(
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

            this.id = model.id

            model
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

        schedulerDAO.saveBatch(schedulers)
        workoutDAO.saveWorkout(workout)
        workoutDAO.saveGroups(workoutGroups)
    }
}