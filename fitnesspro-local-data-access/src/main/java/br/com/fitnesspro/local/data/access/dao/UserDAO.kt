package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.com.fitnesspro.model.general.User

@Dao
abstract class UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(user: User)

    @Query("SELECT EXISTS(SELECT 1 FROM user WHERE email = :email)")
    abstract suspend fun hasUserWithEmail(email: String): Boolean

    @Query("SELECT EXISTS (SELECT 1 FROM user WHERE email = :email and password = :hashedPassword)")
    abstract suspend fun hasUserWithCredentials(email: String, hashedPassword: String): Boolean

    @Query("SELECT * FROM user WHERE id = :id")
    abstract suspend fun findById(id: String): User

    @Query("UPDATE user SET authenticated = 0 WHERE authenticated = 1")
    abstract suspend fun logoutAll()

    @Query("UPDATE user SET authenticated = 1 WHERE email = :email and password = :hashedPassword")
    abstract suspend fun authenticateWithCredentials(email: String, hashedPassword: String)

    @Transaction
    open suspend fun authenticate(email: String, hashedPassword: String) {
        logoutAll()
        authenticateWithCredentials(email, hashedPassword)
    }


}