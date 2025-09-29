package br.com.fitnesspro.local.data.access.dao.health

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.android.room.toolkit.dao.IntegratedMaintenanceDAO
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.health.HealthConnectSteps
import java.util.StringJoiner

@Dao
abstract class HealthConnectStepsDAO : IntegratedMaintenanceDAO<HealthConnectSteps>() {

    @Query("select * from health_connect_steps where id = :id")
    abstract suspend fun findById(id: String): HealthConnectSteps?

    @Query("select exists(select 1 from health_connect_steps where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageSize: Int, personId: String): List<HealthConnectSteps> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select steps.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from health_connect_steps steps ")
            add(" inner join exercise_execution exec on steps.exercise_execution_id = exec.id ")
            add(" inner join exercise ex on exec.exercise_id = ex.id ")
            add(" inner join workout_group wg on ex.workout_group_id = wg.id ")
            add(" inner join workout w on wg.workout_id = w.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")
            add(" and steps.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" limit ? ")

            params.add(personId)
            params.add(personId)
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<HealthConnectSteps>
}