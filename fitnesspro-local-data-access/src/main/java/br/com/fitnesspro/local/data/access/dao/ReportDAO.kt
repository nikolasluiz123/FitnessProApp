package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.to.TOReport
import java.time.LocalDateTime
import java.util.StringJoiner

@Dao
abstract class ReportDAO: IntegratedMaintenanceDAO<Report>() {

    suspend fun getExportationData(reportContext: EnumReportContext, pageSize: Int): List<Report> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select * ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from report ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where report.transmission_state = '${EnumTransmissionState.PENDING.name}' ")

            when (reportContext) {
                EnumReportContext.SCHEDULERS_REPORT -> {
                    add(" and exists ( ")
                    add("                select 1 ")
                    add("                from scheduler_report sr ")
                    add("                where sr.report_id = report.id ")
                    add("            ) ")
                }
            }

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

    suspend fun getStorageExportationData(pageSize: Int): List<Report> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select * ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from report ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where report.storage_transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" and report.transmission_state = '${EnumTransmissionState.TRANSMITTED.name}' ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<Report>

    suspend fun getStorageImportationData(lastUpdateDate: LocalDateTime?): List<Report> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select report.* ")
        }

        val from = getFromStorageImportationData()

        val where = getWhereStorageImportationData(lastUpdateDate, params)

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }

        return executeStorageImportationData(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeStorageImportationData(query: SupportSQLiteQuery): List<Report>

    suspend fun getExistsStorageImportationData(lastUpdateDate: LocalDateTime?): Boolean {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select 1 ")
        }

        val from = getFromStorageImportationData()
        val where = getWhereStorageImportationData(lastUpdateDate, params)

        val sql = StringJoiner(QR_NL).apply {
            add(" select exists ( ")
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(" ) existe ")
        }

        return executeExistsStorageImportationData(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    private fun getFromStorageImportationData(): StringJoiner {
        return StringJoiner(QR_NL).apply {
            add(" from report ")
        }
    }

    private fun getWhereStorageImportationData(lastUpdateDate: LocalDateTime?, params: MutableList<Any>): StringJoiner {
        return StringJoiner(QR_NL).apply {
            add(" where report.storage_url is not null ")
            add(" and report.active = 1 ")

            lastUpdateDate?.let {
                add(" and report.storage_transmission_date >= ? ")
                params.add(it.format(EnumDateTimePatterns.DATE_TIME_SQLITE))
            }
        }
    }

    @RawQuery
    abstract suspend fun executeExistsStorageImportationData(query: SupportSQLiteQuery): Boolean

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
            add(" where report.active = 1 ")

            when (context) {
                EnumReportContext.SCHEDULERS_REPORT -> {
                    add(" and sr.person_id = ? ")
                    add(" and sr.report_context = '${context.name}' ")
                    add(" and sr.active = 1 ")

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

    @Query("select exists (select 1 from report where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean
}