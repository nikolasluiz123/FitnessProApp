package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.sync.EnumSyncModule
import br.com.fitnesspro.model.sync.SyncHistory

@Dao
abstract class SyncHistoryDAO: MaintenanceDAO<SyncHistory>() {

    @Query(" select * from sync_history where module = :module ")
    abstract suspend fun getSyncHistory(module: EnumSyncModule): SyncHistory?

}