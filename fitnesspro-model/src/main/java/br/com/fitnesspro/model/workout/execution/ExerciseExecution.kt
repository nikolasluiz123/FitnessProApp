package br.com.fitnesspro.model.workout.execution

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.health.interfaces.IHealthDataRangeEntity
import java.time.Instant
import java.util.UUID

@Entity(
    tableName = "exercise_execution",
)
data class ExerciseExecution(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    @ColumnInfo(name = "exercise_id", index = true)
    var exerciseId: String? = null,
    var duration: Long? = null,
    var repetitions: Int? = null,
    @ColumnInfo(name = "actual_set")
    var actualSet: Int? = null,
    var rest: Long? = null,
    var weight: Double? = null,
    var active: Boolean = true,
    @ColumnInfo(name = "health_data_collected")
    override var healthDataCollected: Boolean = false,
    @ColumnInfo(name = "execution_start_time")
    var executionStartTime: Instant = Instant.now(),
    @ColumnInfo(name = "execution_end_time")
    var executionEndTime: Instant? = null,
) : IHealthDataRangeEntity {

    @get:Ignore
    override val rangeStartTime: Instant
        get() = this.executionStartTime

    @get:Ignore
    override val rangeEndTime: Instant?
        get() = this.executionEndTime
}