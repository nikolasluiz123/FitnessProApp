package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.to.TOReport
import java.util.StringJoiner

@Dao
abstract class ReportDAO: IntegratedMaintenanceDAO<Report>() {

    suspend fun getListGeneratedReports(
        context: EnumReportContext,
        authenticatedPersonId: String,
        quickFilter: String? = null
    ): List<TOReport> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select report.id as id, ")
            add("        report.name as name, ")
            add("        report.extension as extension, ")
            add("        report.file_path as filePath, ")
            add("        report.date as date, ")
            add("        report.kb_size as kbSize ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from report ")

            when (context) {
                EnumReportContext.SCHEDULERS_REPORT -> {
                    add(" inner join scheduler_report sr on sr.report_id = report.id ")
                }
            }
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where 1 = 1 ")

            when (context) {
                EnumReportContext.SCHEDULERS_REPORT -> {
                    add(" and sr.person_id = ? ")
                    add(" and sr.report_context = '${context.name}' ")

                    params.add(authenticatedPersonId)
                }
            }

            if (!quickFilter.isNullOrEmpty()) {
                add(" and lower(report.name) like ? ")
                params.add("%${quickFilter.lowercase()}%")
            }
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by report.date desc ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(orderBy.toString())
        }

        return executeQueryListGeneratedReports(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeQueryListGeneratedReports(query: SupportSQLiteQuery): List<TOReport>

    @Query("select * from report where id = :id")
    abstract suspend fun getReportById(id: String): Report?

    @Delete
    abstract suspend fun deleteReport(report: Report)

    @Delete
    abstract suspend fun deleteReports(reports: List<Report>)

}