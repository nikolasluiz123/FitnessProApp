package br.com.fitnesspro.model.workout.execution

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.Video
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
        ),
    ]
)
data class VideoExerciseExecution(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    @ColumnInfo(name = "exercise_execution_id", index = true)
    var exerciseExecutionId: String? = null,
    @ColumnInfo(name = "video_id", index = true)
    var videoId: String? = null
): IntegratedModel