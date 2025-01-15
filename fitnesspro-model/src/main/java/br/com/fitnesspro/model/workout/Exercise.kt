package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import java.util.UUID

@Entity(
    tableName = "exercise",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutGroup::class,
            parentColumns = ["id"],
            childColumns = ["workout_group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("workout_group_id")
    ]
)
data class Exercise(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    var duration: Long? = null,
    var repetitions: Int? = null,
    var sets: Int? = null,
    var rest: Long? = null,
    var observation: String? = null,
    @ColumnInfo(name = "workout_group_id")
    var workoutGroupId: String? = null,
    var active: Boolean = true
): BaseModel()