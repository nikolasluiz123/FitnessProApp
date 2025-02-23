package br.com.fitnesspro.local.data.access.dao.common

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.model.base.AuditableModel

abstract class AuditableMaintenanceDAO<T: AuditableModel>: BaseDAO() {

    open suspend fun insert(model: T, userId: String? = null, writeAuditableData: Boolean = false) {
        if (writeAuditableData) {
            model.creationUserId = userId!!
            model.updateUserId = userId
        }

        internalInsert(model)
    }

    open suspend fun insertBatch(models: List<T>, userId: String? = null, writeAuditableData: Boolean = false) {
        if (writeAuditableData) {
            models.forEach {
                it.creationUserId = userId!!
                it.updateUserId = userId
            }
        }

        internalInsertBatch(models)
    }
    
    open suspend fun update(model: T, userId: String? = null, writeAuditableData: Boolean = false) {
        if (writeAuditableData) {
            model.updateDate = dateTimeNow()
            model.updateUserId = userId!!
        }

        internalUpdate(model)
    }

    open suspend fun updateBatch(models: List<T>, userId: String? = null, writeAuditableData: Boolean = false) {
        if (writeAuditableData) {
            models.forEach {
                it.updateDate = dateTimeNow()
                it.updateUserId = userId!!
            }
        }

        internalUpdateBatch(models)
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun internalInsert(model: T)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun internalInsertBatch(models: List<T>)

    @Update
    protected abstract suspend fun internalUpdate(model: T)

    @Update
    protected abstract suspend fun internalUpdateBatch(models: List<T>)
}