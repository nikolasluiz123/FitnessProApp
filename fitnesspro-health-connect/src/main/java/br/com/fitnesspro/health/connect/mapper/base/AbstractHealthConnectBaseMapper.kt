package br.com.fitnesspro.health.connect.mapper.base

import androidx.health.connect.client.records.Record
import br.com.fitnesspro.model.enums.health.EnumDeviceType
import br.com.fitnesspro.model.enums.health.EnumRecordingMethod
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Classe base fundamental para todos os Mappers do Health Connect.
 *
 * Fornece funcionalidades utilitárias compartilhadas, principalmente a extração
 * e conversão dos metadados padrão do [Record] do Health Connect para o
 * modelo de domínio [HealthConnectMetadata].
 *
 * @author Nikolas Luiz Schmitt
 */
abstract class AbstractHealthConnectBaseMapper {

    /**
     * Extrai os metadados de um [Record] do Health Connect e os converte
     * para a entidade de domínio [HealthConnectMetadata].
     *
     * @param record O registro do Health Connect do qual os metadados serão extraídos.
     * @return A entidade [HealthConnectMetadata] preenchida.
     */
    protected fun getMetadataFrom(record: Record): HealthConnectMetadata {
        return HealthConnectMetadata(
            id = record.metadata.id,
            dataOriginPackage = record.metadata.dataOrigin.packageName,
            lastModifiedTime = LocalDateTime.ofInstant(record.metadata.lastModifiedTime, ZoneOffset.UTC),
            clientRecordId = record.metadata.clientRecordId,
            deviceManufacturer = record.metadata.device?.manufacturer,
            deviceModel = record.metadata.device?.model,
            deviceType = EnumDeviceType.entries.firstOrNull { it.ordinal == record.metadata.device?.type },
            recordingMethod = EnumRecordingMethod.entries.firstOrNull { it.ordinal == record.metadata.recordingMethod }
        )
    }
}