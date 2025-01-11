package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup

@Dao
abstract class WorkoutDAO: BaseDAO() {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveWorkout(workout: Workout)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveGroups(workoutGroups: List<WorkoutGroup>)
}