package br.com.fitnesspro.health.connect.mapper

import androidx.health.connect.client.records.StepsRecord
import br.com.android.health.connect.toolkit.mapper.AbstractHealthDataAssociatingMapper
import br.com.android.health.connect.toolkit.mapper.result.SingleRecordMapperResult
import br.com.android.room.toolkit.model.health.interfaces.IHealthConnectMetadata
import br.com.android.room.toolkit.model.health.interfaces.IHealthDataRangeEntity
import br.com.fitnesspro.health.connect.service.StepsService
import br.com.fitnesspro.model.workout.health.HealthConnectSteps

/**
 * Mapper específico para converter dados de [StepsRecord] do Health Connect
 * em entidades de domínio [HealthConnectSteps].
 *
 * Este mapper **APENAS** mapeia registros que possuem uma [IHealthDataRangeEntity]
 * (como uma execução de exercício) correspondente com base na sobreposição de tempo.
 * Registros sem uma associação correspondente são ignorados.
 *
 * @param service A instância de [StepsService] usada para buscar os dados.
 *
 * @see AbstractHealthDataAssociatingMapper
 *
 * @author Nikolas Luiz Schmitt
 */
class StepsMapper(
    service: StepsService
) : AbstractFitnessProHealthDataAssociatingMapper<SingleRecordMapperResult<HealthConnectSteps>, StepsRecord, StepsService>(service) {

    /**
     * Mapeia um [StepsRecord] para [HealthConnectSteps], **se** conseguir associá-lo a uma
     * entidade de execução de exercício.
     *
     * @param record O registro de [StepsRecord] lido.
     * @param metadata Os [HealthConnectMetadata] extraídos.
     * @param associationEntities A lista de entidades (ex: execuções de exercício)
     * com as quais este registro pode ser associado.
     * @return Um [SingleRecordMapperResult] contendo a entidade [HealthConnectSteps] mapeada,
     * ou `null` se nenhuma entidade de associação correspondente for encontrada.
     */
    override suspend fun <T : IHealthDataRangeEntity> continueMappingAndAssociate(
        record: StepsRecord,
        metadata: IHealthConnectMetadata,
        associationEntities: List<T>
    ): SingleRecordMapperResult<HealthConnectSteps>? {
        val matching = findMatchingEntity(record.startTime, record.endTime, associationEntities)
            ?: return null

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