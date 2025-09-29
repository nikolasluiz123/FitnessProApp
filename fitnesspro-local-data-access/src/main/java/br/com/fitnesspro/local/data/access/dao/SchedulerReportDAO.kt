package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.android.room.toolkit.dao.IntegratedMaintenanceDAO
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.general.report.SchedulerReport
import java.util.StringJoiner

@Dao
abstract class SchedulerReportDAO: IntegratedMaintenanceDAO<SchedulerReport>() {

    suspend fun getExportationData(pageSize: Int): List<SchedulerReport> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select * ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from scheduler_report sr ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where sr.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" limit ? ")

            params.add(pageSize)
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }

        return executeQueryExportationData(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<SchedulerReport>

    @Query("select exists (select 1 from scheduler_report where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    @Query("select * from scheduler_report where report_id = :reportId")
    abstract suspend fun getSchedulerReportByReportId(reportId: String): SchedulerReport

    @Query("select * from scheduler_report where report_id in (:reportIds)")
    abstract suspend fun getSchedulerReportByReportIdIn(reportIds: List<String>): List<SchedulerReport>
}