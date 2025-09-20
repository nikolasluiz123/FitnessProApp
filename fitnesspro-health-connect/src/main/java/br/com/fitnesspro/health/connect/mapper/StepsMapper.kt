package br.com.fitnesspro.health.connect.mapper

import androidx.health.connect.client.records.StepsRecord
import br.com.fitnesspro.health.connect.mapper.base.AbstractHealthDataAssociatingMapper
import br.com.fitnesspro.health.connect.mapper.result.SingleRecordMapperResult
import br.com.fitnesspro.health.connect.service.StepsService
import br.com.fitnesspro.model.base.IHealthDataRangeEntity
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata
import br.com.fitnesspro.model.workout.health.HealthConnectSteps

/**
 * Mapper específico para converter dados de [StepsRecord] do Health Connect
 * em entidades de domínio [HealthConnectSteps].
 *
 * Utiliza o [AbstractHealthDataAssociatingMapper] para buscar dados de passos e
 * associá-los a uma entidade de execução de exercício correspondente
 * ([IHealthDataRangeEntity]) com base na sobreposição de tempo.
 *
 * @param service A instância de [StepsService] usada para buscar os dados.
 *
 * @see AbstractHealthDataAssociatingMapper
 *
 * @author Nikolas Luiz Schmitt
 */
class StepsMapper(
    service: StepsService
) : AbstractHealthDataAssociatingMapper<SingleRecordMapperResult<HealthConnectSteps>, StepsRecord, StepsService>(service) {

    /**
     * Mapeia um [StepsRecord] para [HealthConnectSteps] e tenta associá-lo a uma
     * entidade de execução de exercício.
     *
     * @param record O registro de [StepsRecord] lido.
     * @param metadata Os [HealthConnectMetadata] extraídos.
     * @param associationEntities A lista de entidades (ex: execuções de exercício)
     * com as quais este registro pode ser associado.
     * @return Um [SingleRecordMapperResult] contendo a entidade [HealthConnectSteps] mapeada.
     */
    override suspend fun <T : IHealthDataRangeEntity> continueMappingAndAssociate(
        record: StepsRecord,
        metadata: HealthConnectMetadata,
        associationEntities: List<T>
    ): SingleRecordMapperResult<HealthConnectSteps>? {
        val matching = findMatchingEntity(record.startTime, record.endTime, associationEntities)
        if (matching == null) return null

        val steps = HealthConnectSteps(
            healthConnectMetadataId = metadata.id,
            exerciseExecutionId = matching.id,
            count = record.count,
            startTime = record.startTime,
            endTime = record.endTime,
            startZoneOffset = record.startZoneOffset,
            endZoneOffset = record.endZoneOffset
        )

        return SingleRecordMapperResult(steps, metadata)
    }
}