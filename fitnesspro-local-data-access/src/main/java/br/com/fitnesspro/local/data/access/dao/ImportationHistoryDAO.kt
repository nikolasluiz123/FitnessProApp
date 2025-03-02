package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.sync.ImportationHistory

@Dao
abstract class ImportationHistoryDAO: MaintenanceDAO<ImportationHistory>() {

    @Query(" select * from importation_history where module = :module ")
    abstract suspend fun getImportationHistory(module: EnumSyncModule): ImportationHistory?

}