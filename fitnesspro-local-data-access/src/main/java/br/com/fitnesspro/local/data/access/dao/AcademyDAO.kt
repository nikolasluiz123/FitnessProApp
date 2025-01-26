package br.com.fitnesspro.local.data.access.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.tuple.AcademyTuple
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.StringJoiner

@Dao
abstract class AcademyDAO: BaseDAO() {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveAcademyTime(personAcademyTime: PersonAcademyTime)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveAcademiesBatch(academies: List<Academy>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun savePersonAcademyTimesBatch(personAcademyTimes: List<PersonAcademyTime>)

    fun getAcademies(name: String): PagingSource<Int, AcademyTuple> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select id, name ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from academy ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where active = 1 ")

            if (name.isNotEmpty()) {
                add(" and name like ? ")
                params.add("%$name%")
            }
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by name ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(orderBy.toString())
        }

        return executeQueryAcademies(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery(observedEntities = [Scheduler::class])
    abstract fun executeQueryAcademies(query: SupportSQLiteQuery): PagingSource<Int, AcademyTuple>

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
        personAcademyTimeId: String?,
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
            and pat.active = 1
        )
    """)
    abstract suspend fun getAcademiesFromPerson(personId: String): List<Academy>

    @Query("""
        select pat.*
        from person_academy_time pat
        where pat.active = 1
        and pat.person_id = :personId
        and pat.academy_id = :academyId
    """)
    suspend fun getAcademyTimes(personId: String, academyId: String? = null, dayOfWeek: DayOfWeek? = null): List<PersonAcademyTime> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select * ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from person_academy_time pat ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where pat.active = 1 ")
            add(" and pat.person_id = ? ")
            params.add(personId)

            academyId?.let {
                add(" and pat.academy_id = ? ")
                params.add(it)
            }

            dayOfWeek?.let {
                add(" and pat.day_week = ? ")
                params.add(it)
            }
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }

        return executeQueryAcademyTimes(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery(observedEntities = [PersonAcademyTime::class])
    abstract suspend fun executeQueryAcademyTimes(query: SupportSQLiteQuery): List<PersonAcademyTime>

    @Query("select * from person_academy_time where id = :id")
    abstract suspend fun findPersonAcademyTimeById(id: String): PersonAcademyTime
}