package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.authentication.ServiceToken

@Dao
abstract class ServiceTokenDAO: MaintenanceDAO<ServiceToken>() {

    @Query("select * from service_token where id = :id")
    abstract suspend fun findById(id: String): ServiceToken?

    @Query("""
        select jwt_token 
        from service_token 
        where user_id = :userId
        and expiration_date > datetime('now')
        order by expiration_date desc
        limit 1
    """)
    abstract suspend fun findValidTokenByUserId(userId: String): String?

    @Query("""
        select jwt_token 
        from service_token 
        where device_id = :deviceId
        and expiration_date > datetime('now')
        order by expiration_date desc
        limit 1
    """)
    abstract suspend fun findValidTokenByDeviceId(deviceId: String): String?
}