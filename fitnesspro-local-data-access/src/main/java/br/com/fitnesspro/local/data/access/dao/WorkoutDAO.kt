package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.workout.Workout

@Dao
abstract class WorkoutDAO: IntegratedMaintenanceDAO<Workout>()