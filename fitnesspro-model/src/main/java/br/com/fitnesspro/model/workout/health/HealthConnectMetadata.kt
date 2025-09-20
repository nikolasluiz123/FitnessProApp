package br.com.fitnesspro.model.workout.health

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.enums.health.EnumDeviceType
import br.com.fitnesspro.model.enums.health.EnumRecordingMethod
import java.time.LocalDateTime

/**
 * Esta tabela armazenará as informações comuns a todos os registros de saúde do Health Connect.
 *
 * @param id O id (String) vindo de record.metadata.id. É o ID global único do registro no Health Connect.
 * @param transmissionState O estado de transmissão do registro que é enviado ao servidor.
 * @param dataOriginPackage O packageName do app que escreveu o dado (ex: "com.samsung.android.health").
 * @param lastModifiedTime A data/hora da última modificação feita pelo wearable ou pelo aplicativo.
 * @param clientRecordId O ID opcional que o app de origem usa.
 * @param deviceManufacturer O fabricante do dispositivo que gerou o dado (ex: "Samsung").
 * @param deviceModel O modelo do dispositivo (ex: "Galaxy Watch 6").
 * @param recordingMethod A forma de gravação.
 * @param deviceType O tipo de dispositivo.
 */
@Entity(tableName = "health_connect_metadata")
data class HealthConnectMetadata(
    @PrimaryKey
    override var id: String,

    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,

    @ColumnInfo(name = "data_origin_package")
    var dataOriginPackage: String? = null,

    @ColumnInfo(name = "last_modified_time")
    var lastModifiedTime: LocalDateTime? = null,

    @ColumnInfo(name = "client_record_id")
    var clientRecordId: String? = null,

    @ColumnInfo(name = "device_manufacturer")
    var deviceManufacturer: String? = null,

    @ColumnInfo(name = "device_model")
    var deviceModel: String? = null,

    @ColumnInfo(name = "recording_method")
    var recordingMethod: EnumRecordingMethod? = null,

    @ColumnInfo(name = "device_type")
    var deviceType: EnumDeviceType? = null
): IntegratedModel