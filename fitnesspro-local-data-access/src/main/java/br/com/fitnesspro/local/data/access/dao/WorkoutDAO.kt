package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import br.com.fitnesspro.local.data.access.dao.common.AuditableMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.workout.Workout

@Dao
abstract class WorkoutDAO: AuditableMaintenanceDAO<Workout>()