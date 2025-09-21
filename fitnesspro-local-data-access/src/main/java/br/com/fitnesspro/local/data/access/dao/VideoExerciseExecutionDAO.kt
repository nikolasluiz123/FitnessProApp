package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution
import java.util.StringJoiner

@Dao
abstract class VideoExerciseExecutionDAO: IntegratedMaintenanceDAO<VideoExerciseExecution>() {

    @Query("select count(id) from video_exercise_execution where exercise_execution_id = :exerciseExecutionId")
    abstract suspend fun getCountVideosExecution(exerciseExecutionId: String): Int

    @Query("select exists(select 1 from video_exercise_execution where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageSize: Int, personId: String): List<VideoExerciseExecution> {
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
            add(" limit ? ")

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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<VideoExerciseExecution>

    @Query(" select * from video_exercise_execution where exercise_execution_id in (:exerciseIds) and active = 1 ")
    abstract fun getListVideoExecutionActiveFromExercises(exerciseIds: List<String>): List<VideoExerciseExecution>

    @Query(
        """
        select exercise.id as id
        from exercise_execution exercise
        inner join video_exercise_execution vee on exercise.id = vee.exercise_execution_id
        inner join video v on v.id = vee.video_id
        where v.file_path in (:filePaths)
    """
    )
    abstract suspend fun getListExerciseExecutionIdsFromVideoFilePaths(filePaths: List<String>): List<String>

    @Query("""
        select file_path as path
        from video 
        inner join video_exercise_execution on video.id = video_exercise_execution.video_id 
        where video_exercise_execution.exercise_execution_id = :exerciseExecutionId
        and video_exercise_execution.active = 1
        and video.active = 1
    """)
    abstract suspend fun getListVideoFilePathsFromExecution(exerciseExecutionId: String): List<String>
}