package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.model.general.User

@Dao
abstract class PersonDAO: IBaseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun savePerson(person: Person)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveUser(user: User)

    @Transaction
    open suspend fun save(user: User, person: Person) {
        saveUser(user)
        savePerson(person)
    }

    @Query("select * from person where id = :id")
    abstract suspend fun findById(id: String): Person

    @Query("select * from person where user_id = :userId")
    abstract suspend fun findByUserId(userId: String): Person

    @Query("""
        select academy.* 
        from academy
        where academy.active = 1
        and exists (
            select 1
            from person_academy_time pat
            where pat.person_id = :personId
            and pat.academy_id = academy.id
        )
    """)
    abstract suspend fun getAcademies(personId: String): List<Academy>

    @Query("""
        select pat.*
        from person_academy_time pat
        where pat.active = 1
        and pat.person_id = :personId
        and pat.academy_id = :academyId
    """)
    abstract suspend fun getAcademyTimes(personId: String, academyId: String): List<PersonAcademyTime>

}