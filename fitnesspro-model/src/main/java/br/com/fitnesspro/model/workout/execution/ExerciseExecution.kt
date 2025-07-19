package br.com.fitnesspro.model.workout.execution

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.Exercise
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "exercise_execution",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
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
    var date: LocalDateTime = LocalDateTime.now(),
    var active: Boolean = true
): IntegratedModel