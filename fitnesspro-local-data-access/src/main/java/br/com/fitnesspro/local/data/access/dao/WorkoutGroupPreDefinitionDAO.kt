package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition

@Dao
abstract class WorkoutGroupPreDefinitionDAO: IntegratedMaintenanceDAO<WorkoutGroupPreDefinition>() {

    @Query("select * from workout_pre_definition where id = :workoutGroupPreDefinitionId and active = 1")
    abstract suspend fun findById(workoutGroupPreDefinitionId: String?): WorkoutGroupPreDefinition?
}