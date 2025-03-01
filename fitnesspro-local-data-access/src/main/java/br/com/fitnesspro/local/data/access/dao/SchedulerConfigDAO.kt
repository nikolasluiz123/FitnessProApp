package br.com.fitnesspro.local.data.access.dao

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
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import java.util.StringJoiner

@Dao
abstract class SchedulerConfigDAO: AuditableMaintenanceDAO<SchedulerConfig>() {

    @Query("select * from scheduler_config where id = :id")
    abstract suspend fun findSchedulerConfigById(id: String): SchedulerConfig

    @Query("select * from scheduler_config where person_id = :personId")
    abstract suspend fun findSchedulerConfigByPersonId(personId: String): SchedulerConfig?

    @Query("select exists (select 1 from scheduler_config where id = :id)")
    abstract suspend fun hasSchedulerConfigWithId(id: String): Boolean

    suspend fun getExportationData(filter: CommonExportFilter, pageInfos: ExportPageInfos): List<SchedulerConfig> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select * ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from scheduler_config sc ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where (sc.creation_user_id = ? or sc.update_user_id = ?) ")

            filter.lastUpdateDate?.let {
                add(" and sc.update_date >= ? ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<SchedulerConfig>

}