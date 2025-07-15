package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.VideoExercise
import java.util.StringJoiner

@Dao
abstract class VideoExerciseDAO: IntegratedMaintenanceDAO<VideoExercise>() {

    @Query("""
        select file_path as path
        from video 
        inner join video_exercise on video.id = video_exercise.video_id 
        where video_exercise.exercise_id = :exerciseId
    """)
    abstract suspend fun getListVideoFilePathsFromExercise(exerciseId: String): List<String>

    @Query("""
        select file_path as path
        from video 
        inner join video_exercise_execution on video.id = video_exercise_execution.video_id 
        where video_exercise_execution.exercise_execution_id = :exerciseExecutionId
    """)
    abstract suspend fun getListVideoFilePathsFromExecution(exerciseExecutionId: String): List<String>

    @Query(" select count(id) from video_exercise where exercise_id = :exerciseId ")
    abstract suspend fun getCountVideosExercise(exerciseId: String): Int

    @Query(" select exists(select 1 from video_exercise where id = :id) ")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos, personId: String): List<VideoExercise> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select ve.id as id, ")
            add("        ve.exercise_id as exerciseId, ")
            add("        ve.video_id as videoId ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from video_exercise ve ")
            add(" inner join exercise e on ve.exercise_id = e.id ")
            add(" inner join workout_group wg on e.workout_group_id = wg.id ")
            add(" inner join workout w on wg.workout_id = w.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")
            add(" and ve.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<VideoExercise>

    @Query(" select * from video_exercise where exercise_id in (:exerciseIds) ")
    abstract suspend fun getListVideoExerciseFromExercises(exerciseIds: List<String>): List<VideoExercise>

    @Delete
    abstract suspend fun deleteVideosExercise(videos: List<VideoExercise>)

    suspend fun getExistsVideoExerciseTransmitted(
        workoutId: String? = null,
        workoutGroupId: String? = null,
        exerciseId: String? = null
    ): Boolean {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select 1 ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from video_exercise ve ")
            add(" inner join exercise e on ve.exercise_id = e.id ")

            when {
                workoutId != null -> {
                    add(" inner join workout_group wg on e.workout_group_id = wg.id ")
                    add(" inner join workout w on wg.workout_id = w.id ")
                }

                workoutGroupId != null -> {
                    add(" inner join workout_group wg on e.workout_group_id = wg.id ")
                }
            }
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where ve.transmission_state = '${EnumTransmissionState.TRANSMITTED.name}' ")

            when {
                workoutId != null -> {
                    add(" and w.id = ? ")
                    params.add(workoutId)
                }

                workoutGroupId != null -> {
                    add(" and wg.id = ? ")
                    params.add(workoutGroupId)
                }

                exerciseId != null -> {
                    add(" and e.id = ? ")
                    params.add(exerciseId)
                }
            }
        }

        val sql = StringJoiner(QR_NL).apply {
            add(" select exists ( ")
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(" ) as existVideo ")
        }

        return executeQueryExistsVideoExercise(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeQueryExistsVideoExercise(query: SupportSQLiteQuery): Boolean
}