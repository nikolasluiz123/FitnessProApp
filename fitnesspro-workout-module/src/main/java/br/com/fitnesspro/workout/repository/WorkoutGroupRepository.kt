package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.WorkoutWebClient
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.mappers.getTOWorkoutGroup
import br.com.fitnesspro.mappers.getWorkoutGroup
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.to.TOWorkoutGroup
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.decorator.DayWeekExercicesGroupDecorator
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.decorator.WorkoutGroupDecorator
import java.time.DayOfWeek

class WorkoutGroupRepository(
    context: Context,
    private val workoutGroupDAO: WorkoutGroupDAO,
    private val exerciseDAO: ExerciseDAO,
    private val workoutWebClient: WorkoutWebClient
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
        runInTransaction {
            val workoutGroup = saveWorkoutGroupLocally(toWorkoutGroup)
            saveWorkoutGroupRemote(workoutGroup)
        }
    }

    suspend fun saveWorkoutGroupLocally(toWorkoutGroup: TOWorkoutGroup): WorkoutGroup {
        val workoutGroup = toWorkoutGroup.getWorkoutGroup()

        if (toWorkoutGroup.id == null) {
            workoutGroupDAO.insert(workoutGroup)
        } else {
            workoutGroupDAO.update(workoutGroup, true)
        }

        toWorkoutGroup.id = workoutGroup.id

        return workoutGroup
    }

    private suspend fun saveWorkoutGroupRemote(workoutGroup: WorkoutGroup) {
        val response = workoutWebClient.saveWorkoutGroup(getValidToken(), workoutGroup)

        if (response.success) {
            workoutGroupDAO.update(
                model = workoutGroup.copy(transmissionState = EnumTransmissionState.TRANSMITTED),
                writeTransmissionState = true
            )
        }
    }

    suspend fun inactivateWorkoutGroup(workoutGroupId: String): Boolean {
        inactivateWorkoutGroupLocally(workoutGroupId)
        return inactivateWorkoutGroupRemote(workoutGroupId)
    }

    private suspend fun inactivateWorkoutGroupLocally(workoutGroupId: String) {
        val workoutGroup = workoutGroupDAO.findById(workoutGroupId)!!
        workoutGroup.active = false

        workoutGroupDAO.update(workoutGroup, true)
    }

    private suspend fun inactivateWorkoutGroupRemote(workoutGroupId: String): Boolean {
        val response = workoutWebClient.inactivateWorkoutGroup(getValidToken(), workoutGroupId)

        if (response.success) {
            workoutGroupDAO.update(
                model = workoutGroupDAO.findById(workoutGroupId)!!.copy(transmissionState = EnumTransmissionState.TRANSMITTED),
                writeTransmissionState = true
            )
        }

        return response.success
    }

    suspend fun getListWorkoutGroupDecorator(workoutId: String, dayOfWeek: DayOfWeek): List<WorkoutGroupDecorator> {
        val workoutGroups = workoutGroupDAO.getWorkoutGroupsFromWorkout(workoutId, dayOfWeek).onEach {
            it.name = it.name ?: context.getString(R.string.workout_group_default_name)
        }

        val workoutGroupIds = workoutGroups.map { it.id }
        val allExercises = exerciseDAO.getExercisesFromWorkoutGroup(workoutGroupIds)
        val exercisesByGroupId = allExercises.groupBy { it.workoutGroupId!! }

        return workoutGroups.map { workoutGroup ->
            WorkoutGroupDecorator(
                id = workoutGroup.id,
                label = workoutGroup.name!!,
                items = exercisesByGroupId[workoutGroup.id] ?: emptyList()
            )
        }
    }
}