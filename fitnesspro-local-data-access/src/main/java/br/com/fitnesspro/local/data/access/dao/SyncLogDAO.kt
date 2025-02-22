package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.sync.SyncLog

@Dao
abstract class SyncLogDAO: MaintenanceDAO<SyncLog>()