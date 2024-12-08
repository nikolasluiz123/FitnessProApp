package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.fitnesspro.model.general.User

@Dao
abstract class UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(user: User)

    @Query("SELECT EXISTS(SELECT 1 FROM user WHERE email = :email)")
    abstract suspend fun hasUserWithEmail(email: String): Boolean
}