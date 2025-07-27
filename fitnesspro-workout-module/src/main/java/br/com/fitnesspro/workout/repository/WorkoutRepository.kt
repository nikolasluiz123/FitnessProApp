package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.mappers.geTOWorkout
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.current.workout.decorator.CurrentWorkoutDecorator

class WorkoutRepository(
    context: Context,
    private val workoutDAO: WorkoutDAO,
    private val workoutGroupDAO: WorkoutGroupDAO,
    private val personRepository: PersonRepository,
    private val exerciseRepository: ExerciseRepository,
): FitnessProRepository(context) {

    suspend fun getListWorkout(quickFilter: String? = null): List<TOWorkout> {
        val authPersonId = personRepository.getAuthenticatedTOPerson()?.id!!
        return workoutDAO.getWorkoutsFromPersonalTrainer(authPersonId, quickFilter)
    }

    suspend fun findWorkoutById(id: String): TOWorkout? {
        val workout = workoutDAO.findWorkoutById(id)
        return getTOWorkoutFrom(workout)
    }

    private suspend fun getTOWorkoutFrom(workout: Workout?): TOWorkout? {
        val memberName = workout?.academyMemberPersonId?.let { personRepository.findPersonById(it).name }
        val professionalName = workout?.professionalPersonId?.let { personRepository.findPersonById(it).name }

        return workout?.geTOWorkout(
            memberName = memberName,
            professionalName = professionalName
        )
    }

    suspend fun inactivateWorkout(workoutId: String) {
        saveWorkoutInactivatedLocally(workoutId)
        val listWorkoutGroupId = saveWorkoutGroupsInactivatedLocally(workoutId)
        exerciseRepository.inactivateExercisesFromWorkoutGroupLocally(listWorkoutGroupId)
    }

    private suspend fun saveWorkoutInactivatedLocally(workoutId: String) {
        workoutDAO.findWorkoutById(workoutId)?.let {
            it.active = false
            workoutDAO.update(it, true)
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

    suspend fun getCurrentMemberWorkoutList(): List<CurrentWorkoutDecorator> {
        val authPersonId = personRepository.getAuthenticatedTOPerson()?.id!!
        val groups = workoutDAO.getListWorkoutGroupsFromCurrentWorkout(authPersonId)

        return groups
            .groupBy { it.dayWeek }
            .map { (day, groups) ->
                CurrentWorkoutDecorator(
                    dayWeek = day!!,
                    muscularGroups = groups.sortedBy { it.groupOrder }.joinToString(", ") {
                        it.name ?: context.getString(R.string.workout_group_default_name)
                    }
                )
            }
    }

    suspend fun getCurrentMemberWorkout(): TOWorkout? {
        val authPersonId = personRepository.getAuthenticatedTOPerson()?.id!!
        val workout = workoutDAO.getCurrentMemberWorkout(authPersonId)
        return getTOWorkoutFrom(workout)
    }

}