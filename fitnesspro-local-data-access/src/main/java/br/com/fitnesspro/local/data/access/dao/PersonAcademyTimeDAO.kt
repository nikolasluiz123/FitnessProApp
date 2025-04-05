package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.general.PersonAcademyTime
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.StringJoiner

@Dao
abstract class PersonAcademyTimeDAO: IntegratedMaintenanceDAO<PersonAcademyTime>() {

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
                params.add(it.name)
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

    @Query("select exists(select 1 from person_academy_time where id = :personAcademyTimeId)")
    abstract suspend fun hasPersonAcademyTimeWithId(personAcademyTimeId: String): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos): List<PersonAcademyTime> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select * ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from person_academy_time time ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where time.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" limit ? offset ? ")

            params.add(pageInfos.pageSize)
            params.add(pageInfos.pageSize * pageInfos.pageNumber)
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }

        return executeQueryExportationData(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<PersonAcademyTime>
}