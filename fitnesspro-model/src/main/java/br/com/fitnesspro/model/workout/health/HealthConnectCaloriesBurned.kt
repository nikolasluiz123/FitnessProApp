package br.com.fitnesspro.model.workout.health

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IRelationalHealthConnectEntity
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

/**
 * Armazena um registro de calorias ativas queimadas durante um período de tempo.
 * Está ligado aos metadados globais.
 *
 * @param id O ID único desta entidade no banco de dados local.
 * @param transmissionState O estado de transmissão do registro para o servidor.
 * @param healthConnectMetadataId A chave estrangeira (FK) que liga este registro à entrada
 * correspondente na tabela `health_connect_metadata`.
 * @param caloriesInKcal O valor total de calorias queimadas, expresso em quilocalorias (kcal).
 * @param startTime A data/hora de início do período da medição.
 * @param endTime A data/hora de término do período da medição.
 * @param startZoneOffset O fuso horário (em segundos) no momento do `startTime`.
 * @param endZoneOffset O fuso horário (em segundos) no momento do `endTime`.
 */
@Entity(
    tableName = "health_connect_calories_burned",
)
data class HealthConnectCaloriesBurned(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,

    @ColumnInfo(name = "health_connect_metadata_id", index = true)
    var healthConnectMetadataId: String? = null,

    @ColumnInfo(name = "exercise_execution_id", index = true)
    var exerciseExecutionId: String? = null,

    @ColumnInfo(name = "calories_in_kcal")
    var caloriesInKcal: Long? = null,

    @ColumnInfo(name = "start_time")
    var startTime: Instant? = null,

    @ColumnInfo(name = "end_time")
    var endTime: Instant? = null,

    @ColumnInfo(name = "start_zone_offset")
    var startZoneOffset: ZoneOffset? = null,

    @ColumnInfo(name = "end_zone_offset")
    var endZoneOffset: ZoneOffset? = null,
): IntegratedModel, IRelationalHealthConnectEntity {

    override val relationId: String?
        get() = exerciseExecutionId
}
