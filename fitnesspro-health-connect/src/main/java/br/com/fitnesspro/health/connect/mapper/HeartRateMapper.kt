package br.com.fitnesspro.health.connect.mapper

import androidx.health.connect.client.records.HeartRateRecord
import br.com.android.health.connect.toolkit.mapper.AbstractHealthDataAssociatingMapper
import br.com.android.room.toolkit.model.health.interfaces.IHealthConnectMetadata
import br.com.android.room.toolkit.model.health.interfaces.IHealthDataRangeEntity
import br.com.fitnesspro.health.connect.mapper.result.HeartRateMapperResult
import br.com.fitnesspro.health.connect.service.HeartRateService
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRate
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRateSamples

/**
 * Mapper específico para converter dados de [HeartRateRecord] do Health Connect
 * em entidades de domínio [HealthConnectHeartRate] e [HealthConnectHeartRateSamples].
 *
 * Este mapper **APENAS** mapeia registros que possuem uma [IHealthDataRangeEntity]
 * (como uma execução de exercício) correspondente com base na sobreposição de tempo.
 * Registros sem uma associação correspondente são ignorados.
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
) : AbstractFitnessProHealthDataAssociatingMapper<HeartRateMapperResult, HeartRateRecord, HeartRateService>(service) {

    /**
     * Mapeia um [HeartRateRecord] para [HealthConnectHeartRate] (sessão) e uma lista de
     * [HealthConnectHeartRateSamples] (amostras), **se** conseguir associar a sessão a uma
     * entidade de execução de exercício.
     *
     * @param record O registro de [HeartRateRecord] lido.
     * @param metadata Os [HealthConnectMetadata] extraídos.
     * @param associationEntities A lista de entidades (ex: execuções de exercício)
     * com as quais este registro pode ser associado.
     * @return Um [HeartRateMapperResult] contendo a sessão, as amostras e os metadados,
     * ou `null` se nenhuma entidade de associação correspondente for encontrada.
     */
    override suspend fun <T : IHealthDataRangeEntity> continueMappingAndAssociate(
        record: HeartRateRecord,
        metadata: IHealthConnectMetadata,
        associationEntities: List<T>
    ): HeartRateMapperResult? {
        val matching = findMatchingEntity(record.startTime, record.endTime, associationEntities)
            ?: return null

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