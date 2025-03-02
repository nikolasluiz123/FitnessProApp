package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import java.util.StringJoiner

@Dao
abstract class SchedulerConfigDAO: IntegratedMaintenanceDAO<SchedulerConfig>() {

    @Query("select * from scheduler_config where id = :id")
    abstract suspend fun findSchedulerConfigById(id: String): SchedulerConfig

    @Query("select * from scheduler_config where person_id = :personId")
    abstract suspend fun findSchedulerConfigByPersonId(personId: String): SchedulerConfig?

    @Query("select exists (select 1 from scheduler_config where id = :id)")
    abstract suspend fun hasSchedulerConfigWithId(id: String): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos): List<SchedulerConfig> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select * ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from scheduler_config sc ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where sc.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<SchedulerConfig>

}