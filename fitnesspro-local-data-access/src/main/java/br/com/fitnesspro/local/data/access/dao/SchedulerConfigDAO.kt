package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.scheduler.SchedulerConfig

@Dao
abstract class SchedulerConfigDAO: MaintenanceDAO<SchedulerConfig>() {

    @Query("select * from scheduler_config where id = :id")
    abstract suspend fun findSchedulerConfigById(id: String): SchedulerConfig

    @Query("select * from scheduler_config where person_id = :personId")
    abstract suspend fun findSchedulerConfigByPersonId(personId: String): SchedulerConfig?

    @Query("select exists (select 1 from scheduler_config where id = :id)")
    abstract suspend fun hasSchedulerConfigWithId(id: String): Boolean

}