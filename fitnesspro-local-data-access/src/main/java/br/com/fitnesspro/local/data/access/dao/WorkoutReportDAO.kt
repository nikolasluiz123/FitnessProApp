package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.android.room.toolkit.dao.IntegratedMaintenanceDAO
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.general.report.WorkoutReport
import java.util.StringJoiner

@Dao
abstract class WorkoutReportDAO: IntegratedMaintenanceDAO<WorkoutReport>() {

    suspend fun getExportationData(pageSize: Int): List<WorkoutReport> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select * ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from workout_report wr ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where wr.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<WorkoutReport>

    @Query("select exists (select 1 from workout_report where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    @Query("select * from workout_report where report_id = :reportId")
    abstract suspend fun getWorkoutReportByReportId(reportId: String): WorkoutReport

    @Query("select * from workout_report where report_id in (:reportIds)")
    abstract suspend fun getWorkoutReportByReportIdIn(reportIds: List<String>): List<WorkoutReport>
}