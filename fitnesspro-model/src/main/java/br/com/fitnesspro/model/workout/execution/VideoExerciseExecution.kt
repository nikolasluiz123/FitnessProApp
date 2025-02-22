package br.com.fitnesspro.model.workout.execution

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.Video
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "video_exercise_execution",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exercise_execution_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Video::class,
            parentColumns = ["id"],
            childColumns = ["video_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("exercise_execution_id"),
        Index("video_id")
    ]
)
data class VideoExerciseExecution(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,
    @ColumnInfo(name = "exercise_execution_id")
    var exerciseExecutionId: String? = null,
    @ColumnInfo(name = "video_id")
    var videoId: String? = null
): IntegratedModel()