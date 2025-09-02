package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE_SQLITE
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.local.data.access.dao.filters.SchedulerReportFilter
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.CANCELLED
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.COMPLETED
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.CONFIRMED
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.SCHEDULED
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.tuple.reports.schedulers.SchedulerReportTuple
import br.com.fitnesspro.tuple.reports.schedulers.SchedulersResumeSessionReportTuple
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.YearMonth
import java.util.StringJoiner

@Dao
abstract class SchedulerDAO: IntegratedMaintenanceDAO<Scheduler>() {

    @Query("select * from scheduler where id = :id")
    abstract suspend fun findSchedulerById(id: String): Scheduler?

    suspend fun getHasSchedulerConflict(
        schedulerId: String?,
        personId: String,
        userType: EnumUserType,
        start: OffsetDateTime,
        end: OffsetDateTime
    ): Boolean {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select 1 ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from scheduler schedule ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where schedule.active = 1 ")
            add(" and ( ")
            add("       date_time_start between ? and ? ")
            add("       or date_time_end between ? and ? ")
            add("     ) ")
            add(" and schedule.situation != '${CANCELLED}' ")

            val startFormated = start.toString()
            val endFormated = end.toString()

            params.add(startFormated)
            params.add(endFormated)
            params.add(startFormated)
            params.add(endFormated)

            schedulerId?.let {
                add(" and schedule.id != ? ")
                params.add(it)
            }

            when (userType) {
                EnumUserType.PERSONAL_TRAINER,
                EnumUserType.NUTRITIONIST -> {
                    add(" and schedule.professional_person_id = ? ")
                    params.add(personId)
                }

                EnumUserType.ACADEMY_MEMBER -> {
                    add(" and schedule.academy_member_person_id = ? ")
                    params.add(personId)
                }
            }
        }

        val sql = StringJoiner(QR_NL).apply {
            add(" select exists ( ")
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(" ) ")
        }

        return executeQueryHasSchedulerConflict(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery(observedEntities = [Scheduler::class])
    abstract suspend fun executeQueryHasSchedulerConflict(query: SupportSQLiteQuery): Boolean

    suspend fun getSchedulerList(
        personId: String,
        userType: EnumUserType,
        yearMonth: YearMonth? = null,
        scheduledDate: LocalDate? = null,
        canceledSchedules: Boolean = true
    ): List<TOScheduler> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select schedule.id as id, ")
            add("        schedule.academy_member_person_id as academyMemberPersonId, ")
            add("        personMember.name as academyMemberName, ")
            add("        schedule.professional_person_id as professionalPersonId, ")
            add("        personProfessional.name as professionalName, ")
            add("        userProfessional.type as professionalType, ")
            add("        schedule.date_time_start as dateTimeStart, ")
            add("        schedule.date_time_end as dateTimeEnd, ")
            add("        schedule.canceled_date as canceledDate, ")
            add("        schedule.situation as situation, ")
            add("        schedule.compromise_type as compromiseType, ")
            add("        schedule.observation as observation, ")
            add("        schedule.active as active ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from scheduler schedule ")
            add(" inner join person personMember on schedule.academy_member_person_id = personMember.id ")
            add(" inner join person personProfessional on schedule.professional_person_id = personProfessional.id ")
            add(" inner join user userProfessional on personProfessional.user_id = userProfessional.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where schedule.active = 1 ")
            add(" and personMember.active = 1 ")
            add(" and personProfessional.active = 1 ")

            if (!canceledSchedules) {
                add(" and schedule.situation != '${CANCELLED}' ")
            }

            when (userType) {
                EnumUserType.PERSONAL_TRAINER,
                EnumUserType.NUTRITIONIST -> {
                    add(" and schedule.professional_person_id = ? ")
                    params.add(personId)
                }

                EnumUserType.ACADEMY_MEMBER -> {
                    add(" and schedule.academy_member_person_id = ? ")
                    params.add(personId)
                }
            }

            yearMonth?.let {
                val firstDatOfMonth = it.atDay(1).atStartOfDay().format(DATE_SQLITE)
                val lastDayOfMonth = it.atEndOfMonth().atTime(23, 59).format(DATE_SQLITE)

                add(" and schedule.date_time_start between ? and ? ")
                params.add(firstDatOfMonth)
                params.add(lastDayOfMonth)
            }

            scheduledDate?.let {
                add(" and schedule.date_time_start like ? ")
                params.add("${it.format(DATE_SQLITE)}%")
            }
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }

        return executeQuerySchedulerList(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery(observedEntities = [Scheduler::class])
    abstract suspend fun executeQuerySchedulerList(query: SupportSQLiteQuery): List<TOScheduler>

    @Query("select exists ( select 1 from scheduler where id = :id )")
    abstract suspend fun hasEntityWithId(id: String?): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos): List<Scheduler> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select * ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from scheduler s ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where s.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<Scheduler>

    suspend fun getSchedulerReportResume(filter: SchedulerReportFilter): SchedulersResumeSessionReportTuple {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select p.name as personName, ")
            add("        ( ")
            add("           select count(scheduler.id) ")
            add("           from scheduler ")
            add("           where scheduler.active = 1 ")
            add("           and scheduler.professional_person_id = p.id ")
            add("           and scheduler.situation = '${SCHEDULED.name}' ")
            add("        ) as countPending, ")
            add("        ( ")
            add("           select count(scheduler.id) ")
            add("           from scheduler ")
            add("           where scheduler.active = 1 ")
            add("           and scheduler.professional_person_id = p.id ")
            add("           and scheduler.situation = '${CONFIRMED.name}' ")
            add("        ) as countConfirmed, ")
            add("        ( ")
            add("           select count(scheduler.id) ")
            add("           from scheduler ")
            add("           where scheduler.active = 1 ")
            add("           and scheduler.professional_person_id = p.id ")
            add("           and scheduler.situation = '${CANCELLED.name}' ")
            add("        ) as countCanceled, ")
            add("        ( ")
            add("           select count(scheduler.id) ")
            add("           from scheduler ")
            add("           where scheduler.active = 1 ")
            add("           and scheduler.professional_person_id = p.id ")
            add("           and scheduler.situation = '${COMPLETED.name}' ")
            add("        ) as countCompleted ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from scheduler s ")
            add(" inner join person p on s.professional_person_id = p.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            addSchedulerReportCommonFilters(params, filter)
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }

        return executeQuerySchedulerReportResume(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    private fun StringJoiner.addSchedulerReportCommonFilters(params: MutableList<Any>, filter: SchedulerReportFilter) {
        add(" where s.professional_person_id = ? ")
        add(" and s.active = 1 ")

        params.add(filter.personId)

        when {
            filter.dateStart != null && filter.dateEnd != null -> {
                add(" and s.date_time_start between ? and ? ")
                params.add(filter.dateStart.format(DATE_SQLITE))
                params.add(filter.dateEnd.format(DATE_SQLITE))
            }

            filter.dateStart != null -> {
                add(" and s.date_time_start >= ? ")
                params.add(filter.dateStart.format(DATE_SQLITE))
            }

            filter.dateEnd != null -> {
                add(" and s.date_time_start <= ? ")
                params.add(filter.dateEnd.format(DATE_SQLITE))
            }
        }
    }

    @RawQuery
    abstract suspend fun executeQuerySchedulerReportResume(query: SupportSQLiteQuery): SchedulersResumeSessionReportTuple

    suspend fun getListSchedulerReportTuple(filter: SchedulerReportFilter, situation: EnumSchedulerSituation): List<SchedulerReportTuple> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select p.name as personName, ")
            add("        s.date_time_start as dateTimeStart, ")
            add("        s.date_time_end as dateTimeEnd, ")
            add("        s.compromise_type as compromiseType, ")
            add("        s.canceled_date as canceledDate, ")
            add("        cp.name as canceledPersonName ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from scheduler s ")
            add(" inner join person p on s.academy_member_person_id = p.id ")
            add(" left join person cp on s.cancellation_person_id = cp.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            addSchedulerReportCommonFilters(params, filter)

            add(" and s.situation = ? ")
            params.add(situation.name)
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by s.date_time_start desc, s.date_time_end desc ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(orderBy.toString())
        }

        return executeQuerySchedulerReportTuple(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeQuerySchedulerReportTuple(query: SupportSQLiteQuery): List<SchedulerReportTuple>
}