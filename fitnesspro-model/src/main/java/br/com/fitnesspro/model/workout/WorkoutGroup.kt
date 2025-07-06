package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
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
        ),
    ]
)
data class WorkoutGroup(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    var name: String? = null,
    @ColumnInfo(name = "workout_id", index = true)
    var workoutId: String? = null,
    @ColumnInfo(name = "day_week", index = true)
    var dayWeek: DayOfWeek? = null,
    @ColumnInfo(name = "group_order", defaultValue = "1")
    var groupOrder: Int = 1,
    var active: Boolean = true
): IntegratedModel