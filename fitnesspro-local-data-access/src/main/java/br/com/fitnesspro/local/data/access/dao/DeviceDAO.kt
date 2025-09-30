package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.android.room.toolkit.dao.MaintenanceDAO
import br.com.fitnesspro.model.authentication.Device

@Dao
abstract class DeviceDAO: MaintenanceDAO<Device>() {

    @Query("select * from device where id = :id")
    abstract suspend fun findById(id: String): Device?
}