package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution

@Dao
abstract class VideoExerciseExecutionDAO: IntegratedMaintenanceDAO<VideoExerciseExecution>() {

    @Query("select count(id) from video_exercise_execution where exercise_execution_id = :exerciseExecutionId")
    abstract suspend fun getCountVideosExecution(exerciseExecutionId: String): Int
}