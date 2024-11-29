package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "video_exercise",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Video::class,
            parentColumns = ["id"],
            childColumns = ["video_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VideoExercise(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "exercise_id")
    var exerciseId: String? = null,
    @ColumnInfo(name = "video_id")
    var videoId: String? = null
)