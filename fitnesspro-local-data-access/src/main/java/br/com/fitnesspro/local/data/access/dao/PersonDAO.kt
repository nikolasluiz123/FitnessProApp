package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User

@Dao
abstract class PersonDAO{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun savePerson(person: Person)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveUser(user: User)

    @Transaction
    open suspend fun save(user: User, person: Person) {
        saveUser(user)
        savePerson(person)
    }

    @Query("SELECT * FROM person WHERE id = :id")
    abstract suspend fun findById(id: String): Person

    @Query("SELECT * FROM person WHERE user_id = :userId")
    abstract suspend fun findByUserId(userId: String): Person

}