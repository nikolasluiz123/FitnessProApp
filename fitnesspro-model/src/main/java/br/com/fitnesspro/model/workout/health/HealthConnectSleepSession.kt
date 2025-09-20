package br.com.fitnesspro.model.workout.health

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import java.time.Instant
import java.util.UUID

/**
 * Armazena a 'sessão' de sono, que é o contêiner principal para uma noite de sono.
 * Esta entidade agrupa um conjunto de estágios (`HealthConnectSleepStages`)
 * e está ligada aos metadados globais.
 *
 * @param id O ID único desta entidade no banco de dados local.
 * @param transmissionState O estado de transmissão do registro para o servidor.
 * @param healthConnectMetadataId A chave estrangeira (FK) que liga este registro à entrada
 * correspondente na tabela `health_connect_metadata`.
 * @param startTime A data/hora em que a sessão de sono começou (ex: quando o usuário foi para a cama).
 * @param endTime A data/hora em que a sessão de sono terminou (ex: quando o usuário acordou).
 * @param title Um título opcional para a sessão de sono.
 * @param notes Notas ou observações opcionais sobre a sessão de sono.
 */
@Entity(
    tableName = "health_connect_sleep_session",
    foreignKeys = [
        ForeignKey(
            entity = HealthConnectMetadata::class,
            parentColumns = ["id"],
            childColumns = ["health_connect_metadata_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HealthConnectSleepSession(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,

    @ColumnInfo(name = "health_connect_metadata_id", index = true)
    var healthConnectMetadataId: String? = null,

    @ColumnInfo(name = "start_time")
    var startTime: Instant? = null,

    @ColumnInfo(name = "end_time")
    var endTime: Instant? = null,

    var title: String? = null,

    var notes: String? = null,
): IntegratedModel
