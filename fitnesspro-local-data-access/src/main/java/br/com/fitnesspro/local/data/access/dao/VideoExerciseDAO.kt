package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.workout.VideoExercise

@Dao
abstract class VideoExerciseDAO: IntegratedMaintenanceDAO<VideoExercise>() {

    @Query("""
        select file_path as path
        from video 
        inner join video_exercise on video.id = video_exercise.video_id 
        where video_exercise.exercise_id = :exerciseId
    """)
    abstract suspend fun getListVideoFilePathsFromExercise(exerciseId: String): List<String>

    @Query(" select count(id) from video_exercise where exercise_id = :exerciseId ")
    abstract suspend fun getCountVideosExercise(exerciseId: String): Int
}