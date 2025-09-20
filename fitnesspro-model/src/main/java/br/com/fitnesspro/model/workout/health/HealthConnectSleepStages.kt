package br.com.fitnesspro.model.workout.health

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.enums.health.EnumSleepStage
import java.time.Instant
import java.util.UUID

/**
 * Armazena os estágios individuais do sono (ex: leve, profundo, REM)
 * que ocorrem dentro de uma [HealthConnectSleepSession].
 *
 * @param id O ID único desta entidade no banco de dados local.
 * @param transmissionState O estado de transmissão do registro para o servidor.
 * @param healthConnectSleepSessionId A chave estrangeira (FK) que liga este estágio à sua
 * sessão de sono 'pai' na tabela `health_connect_sleep_session`.
 * @param startTime A data/hora de início deste estágio de sono específico.
 * @param endTime A data/hora de término deste estágio de sono específico.
 * @param stage O tipo de estágio do sono, mapeado para um enum local (ex: ACORDADO, LEVE, PROFUNDO, REM).
 */
@Entity(
    tableName = "health_connect_sleep_stages",
    foreignKeys = [
        ForeignKey(
            entity = HealthConnectSleepSession::class,
            parentColumns = ["id"],
            childColumns = ["health_connect_sleep_session_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HealthConnectSleepStages(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,

    @ColumnInfo(name = "health_connect_sleep_session_id", index = true)
    var healthConnectSleepSessionId: String? = null,

    @ColumnInfo(name = "start_time")
    var startTime: Instant? = null,

    @ColumnInfo(name = "end_time")
    var endTime: Instant? = null,

    var stage: EnumSleepStage? = null
): IntegratedModel
