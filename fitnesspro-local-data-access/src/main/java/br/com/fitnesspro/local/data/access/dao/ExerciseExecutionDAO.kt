package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.workout.execution.ExerciseExecution

@Dao
abstract class ExerciseExecutionDAO: IntegratedMaintenanceDAO<ExerciseExecution>() {

    @Query("select * from exercise_execution where id = :id")
    abstract suspend fun findById(id: String): ExerciseExecution?

    @Query("select count(id) + 1 from exercise_execution where exercise_id = :exerciseId")
    abstract suspend fun getActualExecutionSet(exerciseId: String): Int
}