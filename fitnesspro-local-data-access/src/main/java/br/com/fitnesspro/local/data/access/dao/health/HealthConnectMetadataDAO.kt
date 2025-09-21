package br.com.fitnesspro.local.data.access.dao.health

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata
import java.util.StringJoiner

@Dao
abstract class HealthConnectMetadataDAO : IntegratedMaintenanceDAO<HealthConnectMetadata>() {

    @Query("select * from health_connect_metadata where id = :id")
    abstract suspend fun findById(id: String): HealthConnectMetadata?

    @Query("select exists(select 1 from health_connect_metadata where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageSize: Int, personId: String): List<HealthConnectMetadata> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select meta.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from health_connect_metadata meta ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where meta.transmission_state = '${EnumTransmissionState.PENDING.name}' ")

            // Filtro complexo para garantir que estamos exportando apenas metadados
            // que estão ligados a dados relevantes para a pessoa.
            add(" and ( ")

            // 1. Ligado a Steps
            add("     exists ( ")
            add("         select 1 from health_connect_steps steps ")
            add("         inner join exercise_execution exec on steps.exercise_execution_id = exec.id ")
            add("         inner join exercise ex on exec.exercise_id = ex.id ")
            add("         inner join workout_group wg on ex.workout_group_id = wg.id ")
            add("         inner join workout w on wg.workout_id = w.id ")
            add("         where steps.health_connect_metadata_id = meta.id ")
            add("         and (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")
            params.add(personId)
            params.add(personId)
            add("     ) ")

            // 2. Ligado a Calories
            add("     or exists ( ")
            add("         select 1 from health_connect_calories_burned calories ")
            add("         inner join exercise_execution exec on calories.exercise_execution_id = exec.id ")
            add("         inner join exercise ex on exec.exercise_id = ex.id ")
            add("         inner join workout_group wg on ex.workout_group_id = wg.id ")
            add("         inner join workout w on wg.workout_id = w.id ")
            add("         where calories.health_connect_metadata_id = meta.id ")
            add("         and (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")
            params.add(personId)
            params.add(personId)
            add("     ) ")

            // 3. Ligado a Heart Rate
            add("     or exists ( ")
            add("         select 1 from health_connect_heart_rate hr ")
            add("         inner join exercise_execution exec on hr.exercise_execution_id = exec.id ")
            add("         inner join exercise ex on exec.exercise_id = ex.id ")
            add("         inner join workout_group wg on ex.workout_group_id = wg.id ")
            add("         inner join workout w on wg.workout_id = w.id ")
            add("         where hr.health_connect_metadata_id = meta.id ")
            add("         and (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")
            params.add(personId)
            params.add(personId)
            add("     ) ")
            
            // 4. Ligado a Sleep Session (que está associada a uma execução)
            add("     or exists ( ")
            add("         select 1 from health_connect_sleep_session sleep ")
            add("         where sleep.health_connect_metadata_id = meta.id ")
            add("         and exists ( ")
            add("             select 1 from sleep_session_exercise_execution assoc ")
            add("             inner join exercise_execution exec on assoc.exercise_execution_id = exec.id ")
            add("             inner join exercise ex on exec.exercise_id = ex.id ")
            add("             inner join workout_group wg on ex.workout_group_id = wg.id ")
            add("             inner join workout w on wg.workout_id = w.id ")
            add("             where assoc.health_connect_sleep_session_id = sleep.id ")
            add("             and (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")
            params.add(personId)
            params.add(personId)
            add("         ) ")
            add("     ) ")
            
            add(" ) ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<HealthConnectMetadata>
}