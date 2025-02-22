package br.com.fitnesspro.model.workout.predefinition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.workout.Video
import java.time.LocalDateTime
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
    ],
    indices = [
        Index("exercise_pre_definition_id"),
        Index("video_id")
    ]
)
data class VideoExercisePreDefinition(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,
    @ColumnInfo(name = "exercise_pre_definition_id")
    var exercisePreDefinitionId: String? = null,
    @ColumnInfo(name = "video_id")
    var videoId: String? = null
): IntegratedModel()