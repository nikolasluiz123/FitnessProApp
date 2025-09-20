package br.com.fitnesspro.model.workout.health

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import java.util.UUID

@Entity(
    tableName = "sleep_session_exercise_execution",
    foreignKeys = [
        ForeignKey(
            entity = HealthConnectSleepSession::class,
            parentColumns = ["id"],
            childColumns = ["health_connect_sleep_session_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseExecution::class,
            parentColumns = ["id"],
            childColumns = ["exercise_execution_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SleepSessionExerciseExecution(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "transmission_state")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,

    @ColumnInfo(name = "health_connect_sleep_session_id", index = true)
    var healthConnectSleepSessionId: String? = null,

    @ColumnInfo(name = "exercise_execution_id", index = true)
    var exerciseExecutionId: String? = null
) : IntegratedModel