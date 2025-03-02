package br.com.fitnesspro.local.data.access.dao.common

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

abstract class MaintenanceDAO<T: Any>: BaseDAO() {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(model: T)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertBatch(models: List<T>)

    @Update
    abstract suspend fun update(model: T)

    @Update
    abstract suspend fun updateBatch(models: List<T>)
}