package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.local.data.access.dao.IBaseDAO.Companion.QR_NL
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.to.TOScheduler
import java.time.LocalDate
import java.time.YearMonth
import java.util.StringJoiner

@Dao
abstract class SchedulerDAO: IBaseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveConfig(schedulerConfig: SchedulerConfig)

    @Query("select * from scheduler_config where id = :id")
    abstract suspend fun findById(id: String): SchedulerConfig

    @Query("select * from scheduler_config where person_id = :personId")
    abstract suspend fun findByPersonId(personId: String): SchedulerConfig?

    suspend fun getSchedulerList(
        personId: String,
        userType: EnumUserType,
        yearMonth: YearMonth? = null,
        scheduledDate: LocalDate? = null
    ): List<TOScheduler> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select schedule.id as id, ")
            add("        schedule.academy_member_person_id as academyMemberPersonId, ")
            add("        personMember.name as academyMemberName, ")
            add("        schedule.professional_person_id as professionalPersonId, ")
            add("        personProfessional.name as professionalName, ")
            add("        schedule.scheduled_date as scheduledDate, ")
            add("        schedule.start as start, ")
            add("        schedule.end as end, ")
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
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where schedule.active = 1 ")
            add(" and personMember.active = 1 ")
            add(" and personProfessional.active = 1 ")

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
                val firstDatOfMonth = it.atDay(1).atStartOfDay().format(EnumDateTimePatterns.DATE_SQLITE)
                val lastDayOfMonth = it.atEndOfMonth().atTime(23, 59).format(EnumDateTimePatterns.DATE_SQLITE)

                add(" and schedule.scheduled_date between ? and ? ")
                params.add(firstDatOfMonth)
                params.add(lastDayOfMonth)
            }

            scheduledDate?.let {
                add(" and schedule.scheduled_date = ? ")
                params.add(it.format(EnumDateTimePatterns.DATE_SQLITE))
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
}