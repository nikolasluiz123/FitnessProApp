package br.com.fitnesspro.local.data.access.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.tuple.AcademyTuple
import java.util.StringJoiner

@Dao
abstract class AcademyDAO: MaintenanceDAO<Academy>() {

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

}