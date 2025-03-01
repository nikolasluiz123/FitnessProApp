package br.com.fitnesspro.local.data.access.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.local.data.access.dao.common.AuditableMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.CommonExportFilter
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.tuple.PersonTuple
import java.util.StringJoiner

@Dao
abstract class PersonDAO: AuditableMaintenanceDAO<Person>() {

    @Query("select * from person where id = :id")
    abstract suspend fun findPersonById(id: String): Person

    @Query("select * from person where user_id = :userId")
    abstract suspend fun findPersonByUserId(userId: String): Person

    fun getPersonsWithUserType(
        types: List<EnumUserType>,
        simpleFilter: String,
        personsForSchedule: Boolean
    ): PagingSource<Int, PersonTuple> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select person.id as id, ")
            add("        person.name as name, ")
            add("        user.type as userType ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from person ")
            add(" inner join user on user.id = person.user_id ")

            if (personsForSchedule) {
                add(" inner join person_academy_time academy_time on academy_time.person_id = person.id ")
            }
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where person.active = 1 ")
            add(" and user.active = 1 ")

            if (personsForSchedule) {
                add(" and academy_time.active = true ")
                add(" and ( ")
                add("       select 1 ")
                add("       from person_academy_time pat_auth_person ")
                add("       where pat_auth_person.active = 1 ")
                add("       and pat_auth_person.person_id = (  ")
                add("                                         select p.id ")
                add("                                         from person p ")
                add("                                         inner join user u on u.id = p.user_id ")
                add("                                         where p.active = 1 ")
                add("                                         and u.active = 1 ")
                add("                                         and u.authenticated = 1 ")
                add("                                       ) ")
                add("       and pat_auth_person.academy_id = academy_time.academy_id ")
                add("       and pat_auth_person.day_week = academy_time.day_week ")
                add("       and not ( ")
                add("               pat_auth_person.time_end <= academy_time.time_start ")
                add("               or ")
                add("               pat_auth_person.time_start >= academy_time.time_end ")
                add("           ) ")
                add("    )   ")
            }

            if (simpleFilter.isNotEmpty()) {
                add(" and person.name like ? ")
                params.add("%$simpleFilter%")
            }

            add(" and user.type in ")
            concatElementsForIn(types.map(EnumUserType::name), params)
        }

        val groupBy = StringJoiner(QR_NL).apply {
            add(" group by person.id ")
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by person.name ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(groupBy.toString())
            add(orderBy.toString())
        }

        return executeQueryPersonsWithUserType(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery(observedEntities = [Person::class])
    abstract fun executeQueryPersonsWithUserType(query: SupportSQLiteQuery): PagingSource<Int, PersonTuple>

    @Query("select exists (select 1 from person where id = :id)")
    abstract suspend fun hasPersonWithId(id: String): Boolean

    suspend fun getExportationData(filter: CommonExportFilter, pageInfos: ExportPageInfos): List<Person> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select * ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from person p ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where (p.creation_user_id = ? or p.update_user_id = ?) ")

            filter.lastUpdateDate?.let {
                add(" and p.update_date >= ? ")
                params.add(it.format(EnumDateTimePatterns.DATE_TIME_SQLITE))
            }

            add(" limit ? offset ? ")

            params.add(filter.authenticatedUserId)
            params.add(filter.authenticatedUserId)
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<Person>
}