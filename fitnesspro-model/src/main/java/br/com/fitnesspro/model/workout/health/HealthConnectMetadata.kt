package br.com.fitnesspro.model.workout.health

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.health.enums.EnumDeviceType
import br.com.android.room.toolkit.model.health.enums.EnumRecordingMethod
import br.com.android.room.toolkit.model.health.interfaces.IHealthConnectMetadata
import java.time.LocalDateTime

@Entity(tableName = "health_connect_metadata")
data class HealthConnectMetadata(
    @PrimaryKey
    override var id: String,

    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,

    @ColumnInfo(name = "data_origin_package")
    override var dataOriginPackage: String? = null,

    @ColumnInfo(name = "last_modified_time")
    override var lastModifiedTime: LocalDateTime? = null,

    @ColumnInfo(name = "client_record_id")
    override var clientRecordId: String? = null,

    @ColumnInfo(name = "device_manufacturer")
    override var deviceManufacturer: String? = null,

    @ColumnInfo(name = "device_model")
    override var deviceModel: String? = null,

    @ColumnInfo(name = "recording_method")
    override var recordingMethod: EnumRecordingMethod? = null,

    @ColumnInfo(name = "device_type")
    override var deviceType: EnumDeviceType? = null
): IHealthConnectMetadata