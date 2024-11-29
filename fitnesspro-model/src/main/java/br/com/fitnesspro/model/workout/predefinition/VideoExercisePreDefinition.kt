package br.com.fitnesspro.model.workout.predefinition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.workout.Video
import java.util.UUID

@Entity(
    tableName = "video_exercise_pre_definition",
    foreignKeys = [
        ForeignKey(
            entity = ExercisePreDefinition::class,
            parentColumns = ["id"],
            childColumns = ["exercise_pre_definition_id"],
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
data class VideoExercisePreDefinition(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "exercise_pre_definition_id")
    var exercisePreDefinitionId: String? = null,
    @ColumnInfo(name = "video_id")
    var videoId: String? = null
)