package br.com.fitnesspro.local.data.access.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.WorkoutGroup
import java.time.DayOfWeek
import java.util.StringJoiner

@Dao
abstract class WorkoutGroupDAO: IntegratedMaintenanceDAO<WorkoutGroup>() {

    @Query("select * from workout_group where id = :workoutGroupId and active = 1")
    abstract suspend fun findById(workoutGroupId: String?): WorkoutGroup?

    suspend fun getWorkoutGroupsFromWorkout(
        workoutId: String,
        dayOfWeek: DayOfWeek? = null,
        workoutGroupId: String? = null,
        simpleFilter: String? = null
    ): List<WorkoutGroup> {
        val queryParams = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select * ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from workout_group ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where workout_group.workout_id = ? ")
            add(" and workout_group.active = 1 ")

            queryParams.add(workoutId)

            workoutGroupId?.let {
                add(" and workout_group.id = ? ")
                queryParams.add(it)
            }

            dayOfWeek?.let {
                add(" and workout_group.day_week = ? ")
                queryParams.add(it.name)
            }

            if (!simpleFilter.isNullOrEmpty()) {
                add(" and lower(workout_group.name) like ? ")
                queryParams.add("%${simpleFilter.lowercase()}%")
            }
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by workout_group.group_order ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(orderBy.toString())
        }

        return executeQueryWorkoutGroupsFromWorkout(SimpleSQLiteQuery(sql.toString(), queryParams.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeQueryWorkoutGroupsFromWorkout(query: SupportSQLiteQuery): List<WorkoutGroup>

    @Query("""
        select *                         
        from workout_group 
        where workout_id = :workoutId 
        and lower(name) = lower(:name) 
        and active = 1
    """)
    abstract suspend fun findWorkoutGroupByName(workoutId: String, name: String): WorkoutGroup?

    @Query("select exists(select 1 from workout_group where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos, userId: String): List<WorkoutGroup> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select wg.id as id, ")
            add("        wg.name as name, ")
            add("        wg.workout_id as workoutId, ")
            add("        wg.day_week as dayWeek, ")
            add("        wg.group_order as groupOrder, ")
            add("        wg.active as active ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from workout_group wg ")
            add(" inner join workout w on wg.workout_id = w.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")
            add(" and wg.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" limit ? offset ? ")

            params.add(userId)
            params.add(userId)
            params.add(pageInfos.pageSize)
            params.add(pageInfos.pageSize * pageInfos.pageNumber)
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }

        Log.i("Teste", "sql = $sql ")
        Log.i("Teste", "params = ${params.joinToString()} ")

        return executeQueryExportationData(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<WorkoutGroup>

}