package br.com.fitnesspro.model.workout.health

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.health.interfaces.IRelationalHealthConnectEntity
import br.com.android.room.toolkit.model.interfaces.sync.IntegratedModel
import java.util.UUID

@Entity(
    tableName = "sleep_session_exercise_execution",
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
) : IntegratedModel, IRelationalHealthConnectEntity {

    override val relationId: String?
        get() = exerciseExecutionId
}