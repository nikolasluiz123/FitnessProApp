package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.com.fitnesspro.model.general.User

@Dao
abstract class UserDAO: BaseDAO() {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(user: User)

    @Query("select exists(select 1 from user where email = :email and id != :userId)")
    abstract suspend fun hasUserWithEmail(email: String, userId: String): Boolean

    @Query("select exists (select 1 from user where email = :email and password = :hashedPassword)")
    abstract suspend fun hasUserWithCredentials(email: String, hashedPassword: String): Boolean

    @Query("select * from user where id = :id")
    abstract suspend fun findById(id: String): User

    @Query("""
              select user.* 
              from user
              inner join person on person.user_id = user.id
              where person.id = :personId
    """)
    abstract suspend fun findByPersonId(personId: String): User

    @Query("update user set authenticated = 0 where authenticated = 1")
    abstract suspend fun logoutAll()

    @Query("update user set authenticated = 1 where email = :email and password = :hashedPassword")
    abstract suspend fun authenticateWithCredentials(email: String, hashedPassword: String)

    @Transaction
    open suspend fun authenticate(email: String, hashedPassword: String) {
        logoutAll()
        authenticateWithCredentials(email, hashedPassword)
    }

    @Query("select * from user where authenticated = 1")
    abstract suspend fun getAuthenticatedUser(): User?

}