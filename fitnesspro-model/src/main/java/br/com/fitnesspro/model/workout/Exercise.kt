package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import java.util.UUID

@Entity(
    tableName = "exercise",
)
data class Exercise(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    var name: String? = null,
    var duration: Long? = null,
    var repetitions: Int? = null,
    var sets: Int? = null,
    var rest: Long? = null,
    var observation: String? = null,
    @ColumnInfo(name = "workout_group_id", index = true)
    var workoutGroupId: String? = null,
    @ColumnInfo(name = "exercise_order")
    var exerciseOrder: Int? = null,
    var active: Boolean = true
): IntegratedModel