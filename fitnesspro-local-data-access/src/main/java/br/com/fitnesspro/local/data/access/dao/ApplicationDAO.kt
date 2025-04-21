package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.authentication.Application

@Dao
abstract class ApplicationDAO: MaintenanceDAO<Application>() {

    @Query("select * from application where id = :id")
    abstract suspend fun findById(id: String): Application?

}