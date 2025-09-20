package br.com.fitnesspro.local.data.access.dao.health

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.health.HealthConnectCaloriesBurned
import java.util.StringJoiner

@Dao
abstract class HealthConnectCaloriesBurnedDAO : IntegratedMaintenanceDAO<HealthConnectCaloriesBurned>() {

    @Query("select * from health_connect_calories_burned where id = :id")
    abstract suspend fun findById(id: String): HealthConnectCaloriesBurned?

    @Query("select exists(select 1 from health_connect_calories_burned where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos, personId: String): List<HealthConnectCaloriesBurned> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select calories.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from health_connect_calories_burned calories ")
            add(" inner join exercise_execution exec on calories.exercise_execution_id = exec.id ")
            add(" inner join exercise ex on exec.exercise_id = ex.id ")
            add(" inner join workout_group wg on ex.workout_group_id = wg.id ")
            add(" inner join workout w on wg.workout_id = w.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")
            add(" and calories.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" limit ? offset ? ")

            params.add(personId)
            params.add(personId)
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<HealthConnectCaloriesBurned>
}