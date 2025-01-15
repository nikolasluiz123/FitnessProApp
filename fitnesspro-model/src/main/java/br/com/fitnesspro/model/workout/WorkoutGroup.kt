package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import java.time.DayOfWeek
import java.util.UUID

@Entity(
    tableName = "workout_group",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("workout_id")
    ]
)
data class WorkoutGroup(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    @ColumnInfo(name = "workout_id")
    var workoutId: String? = null,
    @ColumnInfo(name = "day_week")
    var dayWeek: DayOfWeek? = null,
    var active: Boolean = true
): BaseModel()