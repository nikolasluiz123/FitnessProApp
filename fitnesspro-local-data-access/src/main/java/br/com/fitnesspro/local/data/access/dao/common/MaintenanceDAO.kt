package br.com.fitnesspro.local.data.access.dao.common

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import br.com.fitnesspro.model.base.BaseModel

abstract class MaintenanceDAO<T: BaseModel>: BaseDAO() {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insert(model: T)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertBatch(models: List<T>)

    @Update
    abstract suspend fun update(model: T)

    @Update
    abstract suspend fun updateBatch(models: List<T>)
}