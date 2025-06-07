package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.mappers.getTOWorkoutGroup
import br.com.fitnesspro.mappers.getWorkoutGroup
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOWorkoutGroup
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.decorator.DayWeekExercicesGroupDecorator
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.decorator.WorkoutGroupDecorator
import java.time.DayOfWeek

class WorkoutGroupRepository(
    context: Context,
    private val workoutGroupDAO: WorkoutGroupDAO,
    private val exerciseDAO: ExerciseDAO
): FitnessProRepository(context) {

    suspend fun getListDayWeekExercisesGroupDecorator(workoutId: String): List<DayWeekExercicesGroupDecorator> {
        val workoutGroups = workoutGroupDAO.getWorkoutGroupsFromWorkout(workoutId).onEach {
            it.name = it.name ?: context.getString(R.string.workout_group_default_name)
        }
        val weeks = workoutGroups.map { it.dayWeek!! }.distinct().sortedBy { it.ordinal }

        val workoutGroupIds = workoutGroups.map { it.id }
        val allExercises = exerciseDAO.getExercisesFromWorkoutGroup(workoutGroupIds)
        val exercisesByGroupId = allExercises.groupBy { it.workoutGroupId!! }

        return weeks.map { week ->
            val groupsFromWeek = workoutGroups.filter { it.dayWeek == week }

            val groupsDecorator = groupsFromWeek.map { workoutGroup ->
                WorkoutGroupDecorator(
                    id = workoutGroup.id,
                    label = workoutGroup.name!!,
                    items = exercisesByGroupId[workoutGroup.id] ?: emptyList()
                )
            }

            DayWeekExercicesGroupDecorator(
                id = week.name,
                label = week.getFirstPartFullDisplayName(),
                items = groupsDecorator
            )
        }
    }

    suspend fun getWorkoutGroupsFromWorkout(
        workoutId: String,
        dayOfWeek: DayOfWeek? = null,
        workoutGroupId: String? = null,
        simpleFilter: String? = null
    ): List<TOWorkoutGroup> {
        return workoutGroupDAO.getWorkoutGroupsFromWorkout(
            workoutId = workoutId,
            dayOfWeek = dayOfWeek,
            workoutGroupId = workoutGroupId,
            simpleFilter = simpleFilter
        ).map {
            val to = it.getTOWorkoutGroup()
            to.name = it.name ?: context.getString(R.string.workout_group_default_name)

            to
        }
    }

    suspend fun findWorkoutGroupByName(workoutId: String, name: String): WorkoutGroup? {
        return workoutGroupDAO.findWorkoutGroupByName(workoutId, name)
    }

    suspend fun findWorkoutGroupById(workoutGroupId: String?): TOWorkoutGroup? {
        return workoutGroupDAO.findById(workoutGroupId)?.getTOWorkoutGroup()
    }

    suspend fun saveWorkoutGroup(toWorkoutGroup: TOWorkoutGroup) {
        saveWorkoutGroupLocally(toWorkoutGroup)
        saveWorkoutGroupRemote(toWorkoutGroup)
    }

    private suspend fun saveWorkoutGroupLocally(toWorkoutGroup: TOWorkoutGroup) {
        val workoutGroup = toWorkoutGroup.getWorkoutGroup()

        if (toWorkoutGroup.id == null) {
            workoutGroupDAO.insert(workoutGroup)
        } else {
            workoutGroupDAO.update(workoutGroup, true)
        }

        toWorkoutGroup.id = workoutGroup.id
    }

    private suspend fun saveWorkoutGroupRemote(toWorkoutGroup: TOWorkoutGroup) {

    }

    suspend fun inactivateWorkoutGroup(workoutGroupId: String) {
        inactivateWorkoutGroupLocally(workoutGroupId)
        inactivateWorkoutGroupRemote(workoutGroupId)
    }

    private suspend fun inactivateWorkoutGroupLocally(workoutGroupId: String) {
        workoutGroupDAO.findById(workoutGroupId)?.let {
            it.active = false
            workoutGroupDAO.update(it, true)
        }
    }

    private suspend fun inactivateWorkoutGroupRemote(workoutGroupId: String) {

    }

    suspend fun saveExerciseWorkoutGroup(toExercise: TOExercise) {
        val toWorkoutGroup = if (toExercise.workoutGroupId == null) {
            TOWorkoutGroup(
                name = toExercise.workoutGroupName,
                workoutId = toExercise.workoutId,
                dayWeek = toExercise.dayWeek,
                order = toExercise.groupOrder
            )
        } else {
            findWorkoutGroupById(toExercise.workoutGroupId)?.apply {
                name = toExercise.workoutGroupName
                order = toExercise.groupOrder
            }
        }

        toWorkoutGroup?.let { to ->
            saveWorkoutGroup(to)
            toExercise.workoutGroupId = to.id
        }
    }
}