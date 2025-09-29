package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.android.room.toolkit.dao.IntegratedMaintenanceDAO
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.VideoExercise
import java.util.StringJoiner

@Dao
abstract class VideoExerciseDAO: IntegratedMaintenanceDAO<VideoExercise>() {

    @Query("""
        select file_path as path
        from video 
        inner join video_exercise on video.id = video_exercise.video_id 
        where video_exercise.exercise_id = :exerciseId
        and video_exercise.active = 1
        and video.active = 1
    """)
    abstract suspend fun getListVideoFilePathsFromExercise(exerciseId: String): List<String>

    @Query(
        """
        select exercise.id as id
        from exercise
        inner join video_exercise ve on exercise.id = ve.exercise_id
        inner join video v on v.id = ve.video_id
        where v.file_path in (:filePaths)
    """
    )
    abstract suspend fun getListExerciseIdsFromVideoFilePaths(filePaths: List<String>): List<String>

    @Query(" select count(id) from video_exercise where exercise_id = :exerciseId ")
    abstract suspend fun getCountVideosExercise(exerciseId: String): Int

    @Query(" select exists(select 1 from video_exercise where id = :id) ")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageSize: Int, personId: String): List<VideoExercise> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select ve.* ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<VideoExercise>

    @Query(" select * from video_exercise where exercise_id in (:exerciseIds) and active = 1 ")
    abstract suspend fun getListVideoExerciseActiveFromExercises(exerciseIds: List<String>): List<VideoExercise>

    @RawQuery
    abstract suspend fun executeQueryExistsVideoExercise(query: SupportSQLiteQuery): Boolean
}