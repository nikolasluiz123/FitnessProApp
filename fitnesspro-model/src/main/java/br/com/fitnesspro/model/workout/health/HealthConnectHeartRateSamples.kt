package br.com.fitnesspro.model.workout.health

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.interfaces.sync.IntegratedModel
import java.time.Instant
import java.util.UUID

/**
 * Armazena cada amostra individual de frequência cardíaca.
 * Esta tabela é filha de `health_connect_heart_rate` e representa os pontos de dados reais.
 *
 * @param id O ID único desta entidade no banco de dados local.
 * @param transmissionState O estado de transmissão do registro para o servidor.
 * @param healthConnectHeartRateId A chave estrangeira (FK) que liga esta amostra ao seu
 * registro 'pai' (o contêiner) na tabela `health_connect_heart_rate`.
 * @param sampleTime O carimbo de data/hora exato desta medição de frequência cardíaca.
 * @param bpm O valor dos batimentos por minuto (BPM) registrado naquele exato momento.
 */
@Entity(
    tableName = "health_connect_heart_rate_samples",
)
data class HealthConnectHeartRateSamples(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,

    @ColumnInfo(name = "health_connect_heart_rate_id", index = true)
    var healthConnectHeartRateId: String? = null,

    @ColumnInfo(name = "sample_time")
    var sampleTime: Instant? = null,

    var bpm: Long? = null
): IntegratedModel
