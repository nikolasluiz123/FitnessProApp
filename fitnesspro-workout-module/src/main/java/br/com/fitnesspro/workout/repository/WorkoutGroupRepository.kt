package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.decorator.DayWeekExercicesGroupDecorator
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.decorator.WorkoutGroupDecorator

class WorkoutGroupRepository(
    context: Context,
    private val workoutGroupDAO: WorkoutGroupDAO,
    private val exerciseDAO: ExerciseDAO
): FitnessProRepository(context) {

    suspend fun getListDayWeekExercisesGroupDecorator(workoutId: String): List<DayWeekExercicesGroupDecorator> {
        val workoutGroups = workoutGroupDAO.getWorkoutGroupsFromWorkout(workoutId)
        val weeks = workoutGroups.map { it.dayWeek!! }.distinct()

        val workoutGroupIds = workoutGroups.map { it.id }
        val allExercises = exerciseDAO.getExercisesFromWorkoutGroup(workoutGroupIds)
        val exercisesByGroupId = allExercises.groupBy { it.workoutGroupId!! }

        return weeks.map { week ->
            val groupsFromWeek = workoutGroups.filter { it.dayWeek == week }

            val groupsDecorator = groupsFromWeek.map { workoutGroup ->
                WorkoutGroupDecorator(
                    id = workoutGroup.id,
                    label = workoutGroup.name ?: context.getString(R.string.workout_group_default_name),
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
}