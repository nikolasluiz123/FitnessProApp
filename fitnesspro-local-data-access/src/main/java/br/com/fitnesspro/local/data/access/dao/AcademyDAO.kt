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
abstract class AcademyDAO: IBaseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveAcademyTime(personAcademyTime: PersonAcademyTime)

    @Query("select * from academy")
    abstract suspend fun getAcademies(): List<Academy>

    @Query("select * from academy where id = :id")
    abstract suspend fun getAcademyById(id: String): Academy

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
    """)
    abstract suspend fun getConflictPersonAcademyTime(
        personId: String,
        dayOfWeek: DayOfWeek,
        start: LocalTime,
        end: LocalTime
    ): PersonAcademyTime?

}