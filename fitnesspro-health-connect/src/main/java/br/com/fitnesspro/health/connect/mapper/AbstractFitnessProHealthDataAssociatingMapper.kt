package br.com.fitnesspro.health.connect.mapper

import androidx.health.connect.client.records.Record
import br.com.android.health.connect.toolkit.mapper.AbstractHealthDataAssociatingMapper
import br.com.android.health.connect.toolkit.mapper.result.IRecordMapperResult
import br.com.android.health.connect.toolkit.service.AbstractBaseHealthConnectService
import br.com.android.room.toolkit.model.health.enums.EnumDeviceType
import br.com.android.room.toolkit.model.health.enums.EnumRecordingMethod
import br.com.android.room.toolkit.model.health.interfaces.IHealthConnectMetadata
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata
import java.time.LocalDateTime
import java.time.ZoneOffset

abstract class AbstractFitnessProHealthDataAssociatingMapper<RESULT : IRecordMapperResult,
                                                             RECORD : Record,
                                                             SERVICE : AbstractBaseHealthConnectService<RECORD>>(service: SERVICE)
    : AbstractHealthDataAssociatingMapper<RESULT, RECORD, SERVICE>(service) {

    override fun getMetadataFrom(record: Record): IHealthConnectMetadata {
        return HealthConnectMetadata(
            id = record.metadata.id,
            dataOriginPackage = record.metadata.dataOrigin.packageName,
            lastModifiedTime = LocalDateTime.ofInstant(
                record.metadata.lastModifiedTime,
                ZoneOffset.UTC
            ),
            clientRecordId = record.metadata.clientRecordId,
            deviceManufacturer = record.metadata.device?.manufacturer,
            deviceModel = record.metadata.device?.model,
            deviceType = EnumDeviceType.entries.firstOrNull { it.ordinal == record.metadata.device?.type },
            recordingMethod = EnumRecordingMethod.entries.firstOrNull { it.ordinal == record.metadata.recordingMethod }
        )
    }
}