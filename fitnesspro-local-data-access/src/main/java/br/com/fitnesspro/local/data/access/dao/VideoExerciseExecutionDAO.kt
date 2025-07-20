package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution
import java.util.StringJoiner

@Dao
abstract class VideoExerciseExecutionDAO: IntegratedMaintenanceDAO<VideoExerciseExecution>() {

    @Query("select count(id) from video_exercise_execution where exercise_execution_id = :exerciseExecutionId")
    abstract suspend fun getCountVideosExecution(exerciseExecutionId: String): Int

    @Query("select exists(select 1 from video_exercise_execution where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos, personId: String): List<VideoExerciseExecution> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select v.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from video_exercise_execution v ")
            add(" inner join exercise_execution e on e.id = v.exercise_execution_id ")
            add(" inner join exercise ex on ex.id = e.exercise_id ")
            add(" inner join workout_group wg on ex.workout_group_id = wg.id ")
            add(" inner join workout w on wg.workout_id = w.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where w.academy_member_person_id = ? ")
            add(" and v.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<VideoExerciseExecution>
}