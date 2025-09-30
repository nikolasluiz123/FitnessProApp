package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.interfaces.sync.IntegratedModel
import java.time.DayOfWeek
import java.util.UUID

@Entity(
    tableName = "workout_group",
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