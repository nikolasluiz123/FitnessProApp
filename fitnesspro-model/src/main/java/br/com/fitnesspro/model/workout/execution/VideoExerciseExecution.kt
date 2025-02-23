package br.com.fitnesspro.model.workout.execution

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.general.User
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
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["creation_user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["update_user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("exercise_execution_id"),
        Index("video_id"),
        Index("creation_user_id"),
        Index("update_user_id")
    ]
)
data class VideoExerciseExecution(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,
	@ColumnInfo(name = "creation_date")
    override var creationDate: LocalDateTime = dateTimeNow(),
    @ColumnInfo(name = "update_date")
    override var updateDate: LocalDateTime = dateTimeNow(),
    @ColumnInfo(name = "creation_user_id")
    override var creationUserId: String = "",
    @ColumnInfo(name = "update_user_id")
    override var updateUserId: String = "",
    @ColumnInfo(name = "exercise_execution_id")
    var exerciseExecutionId: String? = null,
    @ColumnInfo(name = "video_id")
    var videoId: String? = null
): IntegratedModel()