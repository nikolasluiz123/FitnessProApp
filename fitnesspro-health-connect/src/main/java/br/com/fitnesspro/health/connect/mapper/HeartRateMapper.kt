package br.com.fitnesspro.health.connect.mapper

import androidx.health.connect.client.records.HeartRateRecord
import br.com.fitnesspro.health.connect.mapper.base.AbstractHealthDataAssociatingMapper
import br.com.fitnesspro.health.connect.mapper.result.HeartRateMapperResult
import br.com.fitnesspro.health.connect.service.HeartRateService
import br.com.fitnesspro.model.base.IHealthDataRangeEntity
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRate
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRateSamples
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata

/**
 * Mapper específico para converter dados de [HeartRateRecord] do Health Connect
 * em entidades de domínio [HealthConnectHeartRate] e [HealthConnectHeartRateSamples].
 *
 * Associa a sessão de frequência cardíaca a uma execução de exercício ([IHealthDataRangeEntity])
 * com base na sobreposição de tempo.
 *
 * @param service A instância de [HeartRateService] usada para buscar os dados.
 *
 * @see AbstractHealthDataAssociatingMapper
 * @see HeartRateMapperResult
 *
 * @author Nikolas Luiz Schmitt
 */
class HeartRateMapper(
    service: HeartRateService
) : AbstractHealthDataAssociatingMapper<HeartRateMapperResult, HeartRateRecord, HeartRateService>(service) {

    /**
     * Mapeia um [HeartRateRecord] para [HealthConnectHeartRate] (sessão) e uma lista de
     * [HealthConnectHeartRateSamples] (amostras), e tenta associar a sessão a uma
     * entidade de execução de exercício.
     *
     * @param record O registro de [HeartRateRecord] lido.
     * @param metadata Os [HealthConnectMetadata] extraídos.
     * @param associationEntities A lista de entidades (ex: execuções de exercício)
     * com as quais este registro pode ser associado.
     * @return Um [HeartRateMapperResult] contendo a sessão, as amostras e os metadados.
     */
    override suspend fun <T : IHealthDataRangeEntity> continueMappingAndAssociate(
        record: HeartRateRecord,
        metadata: HealthConnectMetadata,
        associationEntities: List<T>
    ): HeartRateMapperResult? {
        val matching = findMatchingEntity(record.startTime, record.endTime, associationEntities)
        if (matching == null) return null

        val heartRateSession = HealthConnectHeartRate(
            healthConnectMetadataId = metadata.id,
            exerciseExecutionId = matching.id,
            startTime = record.startTime,
            endTime = record.endTime,
            startZoneOffset = record.startZoneOffset,
            endZoneOffset = record.endZoneOffset,
        )

        val samples = record.samples.map { sample ->
            HealthConnectHeartRateSamples(
                healthConnectHeartRateId = heartRateSession.id,
                sampleTime = sample.time,
                bpm = sample.beatsPerMinute
            )
        }

        return HeartRateMapperResult(heartRateSession, samples, metadata)
    }
}