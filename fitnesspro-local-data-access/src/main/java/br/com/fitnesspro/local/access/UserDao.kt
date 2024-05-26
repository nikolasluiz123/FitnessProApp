package br.com.fitnesspro.local.access

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.fitnesspro.model.User

@Dao
abstract class UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveUser(user: User)

    @Query("select exists(select * from users where email = :email)")
    abstract suspend fun isEmailUnique(email: String): Boolean

    @Query("select * from users where id = :id")
    abstract suspend fun findUserById(id: String): User
}