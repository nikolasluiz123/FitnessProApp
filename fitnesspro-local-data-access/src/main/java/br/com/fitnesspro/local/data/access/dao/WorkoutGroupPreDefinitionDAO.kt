package br.com.fitnesspro.local.data.access.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition
import java.util.StringJoiner

@Dao
abstract class WorkoutGroupPreDefinitionDAO: IntegratedMaintenanceDAO<WorkoutGroupPreDefinition>() {

    @Query("select * from workout_group_pre_definition where id = :workoutGroupPreDefinitionId")
    abstract suspend fun findById(workoutGroupPreDefinitionId: String?): WorkoutGroupPreDefinition?

    @Query("select exists(select 1 from workout_group_pre_definition where id = :workoutGroupPreDefinitionId)")
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

    fun getListPreDefinitions(authenticatedPersonId: String, simpleFilter: String): PagingSource<Int, TOWorkoutGroupPreDefinition> {
        val queryParams = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select w.id as id, ")
            add("        w.name as name, ")
            add("        w.personal_trainer_person_id as personalTrainerPersonId, ")
            add("        w.active as active ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from workout_group_pre_definition w ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where w.personal_trainer_person_id = ? ")
            add(" and w.active = 1 ")

            queryParams.add(authenticatedPersonId)

            if (simpleFilter.isNotEmpty()) {
                add(" and lower(w.name) like ? ")
                queryParams.add("%${simpleFilter.lowercase()}%")
            }
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by w.name ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(orderBy.toString())
        }

        return executePreDefinitions(SimpleSQLiteQuery(sql.toString(), queryParams.toTypedArray()))
    }

    @RawQuery(observedEntities = [ExercisePreDefinition::class])
    abstract fun executePreDefinitions(query: SupportSQLiteQuery): PagingSource<Int, TOWorkoutGroupPreDefinition>

}