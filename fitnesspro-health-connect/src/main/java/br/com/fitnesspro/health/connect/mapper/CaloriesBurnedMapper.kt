package br.com.fitnesspro.health.connect.mapper

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import br.com.android.health.connect.toolkit.mapper.AbstractHealthDataAssociatingMapper
import br.com.android.health.connect.toolkit.mapper.result.SingleRecordMapperResult
import br.com.android.room.toolkit.model.health.interfaces.IHealthConnectMetadata
import br.com.android.room.toolkit.model.health.interfaces.IHealthDataRangeEntity
import br.com.fitnesspro.health.connect.service.CaloriesBurnedService
import br.com.fitnesspro.model.workout.health.HealthConnectCaloriesBurned
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata

/**
 * Mapper específico para converter dados de [ActiveCaloriesBurnedRecord] do Health Connect
 * em entidades de domínio [HealthConnectCaloriesBurned].
 *
 * Este mapper **APENAS** mapeia registros que possuem uma [IHealthDataRangeEntity]
 * (como uma execução de exercício) correspondente com base na sobreposição de tempo.
 * Registros sem uma associação correspondente são ignorados.
 *
 * @param service A instância de [CaloriesBurnedService] usada para buscar os dados.
 *
 * @see AbstractHealthDataAssociatingMapper
 *
 * @author Nikolas Luiz Schmitt
 */
class CaloriesBurnedMapper(
    service: CaloriesBurnedService
) : AbstractFitnessProHealthDataAssociatingMapper<SingleRecordMapperResult<HealthConnectCaloriesBurned>, ActiveCaloriesBurnedRecord, CaloriesBurnedService>(service) {

    /**
     * Mapeia um [ActiveCaloriesBurnedRecord] para [HealthConnectCaloriesBurned], **se**
     * conseguir associá-lo a uma entidade de execução de exercício.
     *
     * @param record O registro de [ActiveCaloriesBurnedRecord] lido.
     * @param metadata Os [HealthConnectMetadata] extraídos.
     * @param associationEntities A lista de entidades (ex: execuções de exercício)
     * com as quais este registro pode ser associado.
     * @return Um [SingleRecordMapperResult] contendo a entidade [HealthConnectCaloriesBurned] mapeada,
     * ou `null` se nenhuma entidade de associação correspondente for encontrada.
     */
    override suspend fun <T : IHealthDataRangeEntity> continueMappingAndAssociate(
        record: ActiveCaloriesBurnedRecord,
        metadata: IHealthConnectMetadata,
        associationEntities: List<T>
    ): SingleRecordMapperResult<HealthConnectCaloriesBurned>? {
        val matching = findMatchingEntity(record.startTime, record.endTime, associationEntities)
            ?: return null

        val calories = HealthConnectCaloriesBurned(
            healthConnectMetadataId = metadata.id,
            exerciseExecutionId = matching.id,
            caloriesInKcal = record.energy.inKilocalories.toLong(),
            startTime = record.startTime,
            endTime = record.endTime,
            startZoneOffset = record.startZoneOffset,
            endZoneOffset = record.endZoneOffset
        )

        return SingleRecordMapperResult(calories, metadata)
    }
}