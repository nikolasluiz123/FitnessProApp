package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.PersonAcademyTime
import java.time.DayOfWeek
import java.time.LocalTime

@Dao
abstract class AcademyDAO: BaseDAO() {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveAcademyTime(personAcademyTime: PersonAcademyTime)

    @Query("select * from academy")
    abstract suspend fun getAcademies(): List<Academy>

    @Query("select * from academy where id = :id")
    abstract suspend fun findAcademyById(id: String): Academy

    @Query("""
        select *
        from person_academy_time pat
        where pat.active
        and pat.person_id = :personId
        and pat.day_week = :dayOfWeek
        and (
            pat.time_start between :start and :end
            or pat.time_end between :start and :end
        )
        and pat.id != :personAcademyTimeId
    """)
    abstract suspend fun getConflictPersonAcademyTime(
        personAcademyTimeId: String,
        personId: String,
        dayOfWeek: DayOfWeek,
        start: LocalTime,
        end: LocalTime
    ): PersonAcademyTime?

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

    @Query("select * from person_academy_time where id = :id")
    abstract suspend fun findPersonAcademyTimeById(id: String): PersonAcademyTime
}