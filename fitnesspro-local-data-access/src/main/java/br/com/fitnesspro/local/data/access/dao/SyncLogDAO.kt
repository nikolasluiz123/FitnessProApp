package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.sync.SyncLog

@Dao
abstract class SyncLogDAO: MaintenanceDAO<SyncLog>() {

    @Query(" select * from sync_log where id = :id ")
    abstract suspend fun findById(id: String): SyncLog

}