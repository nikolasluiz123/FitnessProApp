package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.mappers.geTOWorkout
import br.com.fitnesspro.to.TOWorkout

class WorkoutRepository(
    context: Context,
    private val workoutDAO: WorkoutDAO,
    private val personRepository: PersonRepository
): FitnessProRepository(context) {

    suspend fun getListWorkout(quickFilter: String? = null): List<TOWorkout> {
        val authPersonId = personRepository.getAuthenticatedTOPerson()?.id!!
        return workoutDAO.getWorkoutsFromPersonalTrainer(authPersonId, quickFilter)
    }

    suspend fun findWorkoutById(id: String): TOWorkout? {
        val workout = workoutDAO.findWorkoutById(id)
        val memberName = workout?.academyMemberPersonId?.let { personRepository.findPersonById(it).name }
        val professionalName = workout?.professionalPersonId?.let { personRepository.findPersonById(it).name }

        return workout?.geTOWorkout(
            memberName = memberName,
            professionalName = professionalName
        )
    }
}