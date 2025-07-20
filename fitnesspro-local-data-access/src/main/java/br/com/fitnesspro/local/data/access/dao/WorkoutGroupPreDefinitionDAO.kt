package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition
import java.util.StringJoiner

@Dao
abstract class WorkoutGroupPreDefinitionDAO: IntegratedMaintenanceDAO<WorkoutGroupPreDefinition>() {

    @Query("select * from workout_group_pre_definition where id = :workoutGroupPreDefinitionId and active = 1")
    abstract suspend fun findById(workoutGroupPreDefinitionId: String?): WorkoutGroupPreDefinition?

    @Query("select exists(select 1 from workout_group_pre_definition where id = :workoutGroupPreDefinitionId and active = 1)")
    abstract suspend fun hasEntityWithId(workoutGroupPreDefinitionId: String?): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos, personId: String): List<WorkoutGroupPreDefinition> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select w.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from workout_group_pre_definition w ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where w.personal_trainer_person_id = ? ")
            add(" and w.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" limit ? offset ? ")

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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<WorkoutGroupPreDefinition>

}