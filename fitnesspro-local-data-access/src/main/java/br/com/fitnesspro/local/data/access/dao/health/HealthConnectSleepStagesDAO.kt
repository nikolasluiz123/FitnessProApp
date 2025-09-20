package br.com.fitnesspro.local.data.access.dao.health

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.health.HealthConnectSleepStages
import java.util.StringJoiner

@Dao
abstract class HealthConnectSleepStagesDAO : IntegratedMaintenanceDAO<HealthConnectSleepStages>() {

    @Query("select * from health_connect_sleep_stages where id = :id")
    abstract suspend fun findById(id: String): HealthConnectSleepStages?

    @Query("select exists(select 1 from health_connect_sleep_stages where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos, personId: String): List<HealthConnectSleepStages> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select stage.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from health_connect_sleep_stages stage ")
            add(" inner join health_connect_sleep_session session on stage.health_connect_sleep_session_id = session.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where stage.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            // Garante que o PAI (SleepSession) está ligado a uma execução válida
            add(" and exists ( ")
            add("     select 1 from sleep_session_exercise_execution assoc ")
            add("     inner join exercise_execution exec on assoc.exercise_execution_id = exec.id ")
            add("     inner join exercise ex on exec.exercise_id = ex.id ")
            add("     inner join workout_group wg on ex.workout_group_id = wg.id ")
            add("     inner join workout w on wg.workout_id = w.id ")
            add("     where assoc.health_connect_sleep_session_id = session.id ")
            add("     and (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")
            params.add(personId)
            params.add(personId)
            add(" ) ")

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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<HealthConnectSleepStages>
}