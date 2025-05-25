package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.workout.WorkoutGroup

@Dao
abstract class WorkoutGroupDAO: IntegratedMaintenanceDAO<WorkoutGroup>() {

    @Query("select * from workout_group where workout_id = :workoutId and active = 1")
    abstract suspend fun getWorkoutGroupsFromWorkout(workoutId: String): List<WorkoutGroup>

}