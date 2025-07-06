package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.WorkoutWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.mappers.geTOWorkout
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.to.TOWorkout

class WorkoutRepository(
    context: Context,
    private val workoutDAO: WorkoutDAO,
    private val workoutGroupDAO: WorkoutGroupDAO,
    private val personRepository: PersonRepository,
    private val exerciseRepository: ExerciseRepository,
    private val workoutWebClient: WorkoutWebClient
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

    suspend fun inactivateWorkout(workoutId: String) {
        val success = inactivateWorkoutRemote(workoutId)
        inactivateWorkoutLocally(workoutId, success)
    }

    private suspend fun inactivateWorkoutLocally(workoutId: String, remoteSuccess: Boolean) {
        saveWorkoutInactivatedLocally(workoutId, remoteSuccess)
        val listWorkoutGroupId = saveWorkoutGroupsInactivatedLocally(workoutId)
        exerciseRepository.inactivateExercisesFromWorkoutGroupLocally(listWorkoutGroupId, remoteSuccess)
    }

    private suspend fun saveWorkoutInactivatedLocally(workoutId: String, remoteSuccess: Boolean) {
        workoutDAO.findWorkoutById(workoutId)?.let {
            it.active = false

            if (remoteSuccess) {
                it.transmissionState = EnumTransmissionState.TRANSMITTED
            }

            workoutDAO.update(it)
        }
    }

    private suspend fun saveWorkoutGroupsInactivatedLocally(workoutId: String): List<String> {
        val ids = mutableListOf<String>()

        val workoutGroups = workoutGroupDAO.getWorkoutGroupsFromWorkout(workoutId).onEach {
            it.active = false
            ids.add(it.id)
        }

        workoutGroupDAO.updateBatch(workoutGroups, true)

        return ids
    }

    private suspend fun inactivateWorkoutRemote(workoutId: String): Boolean {
        val response = workoutWebClient.inactivateWorkout(
            token = getValidToken(),
            workoutId = workoutId
        )

        return response.success
    }


}