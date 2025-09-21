package br.com.fitnesspro.local.data.access.dao.health

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRateSamples
import java.util.StringJoiner

@Dao
abstract class HealthConnectHeartRateSamplesDAO : IntegratedMaintenanceDAO<HealthConnectHeartRateSamples>() {

    @Query("select * from health_connect_heart_rate_samples where id = :id")
    abstract suspend fun findById(id: String): HealthConnectHeartRateSamples?

    @Query("select exists(select 1 from health_connect_heart_rate_samples where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageSize: Int, personId: String): List<HealthConnectHeartRateSamples> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select sample.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from health_connect_heart_rate_samples sample ")
            add(" inner join health_connect_heart_rate hr on sample.health_connect_heart_rate_id = hr.id ")
            add(" inner join exercise_execution exec on hr.exercise_execution_id = exec.id ")
            add(" inner join exercise ex on exec.exercise_id = ex.id ")
            add(" inner join workout_group wg on ex.workout_group_id = wg.id ")
            add(" inner join workout w on wg.workout_id = w.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")
            add(" and sample.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<HealthConnectHeartRateSamples>
}