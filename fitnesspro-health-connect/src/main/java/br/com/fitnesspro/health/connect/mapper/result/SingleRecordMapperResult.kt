package br.com.fitnesspro.health.connect.mapper.result

import br.com.fitnesspro.model.base.IRelationalHealthConnectEntity
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata

/**
 * Wrapper genérico para resultados de mapeamento que consistem em uma única
 * entidade de domínio (além dos metadados).
 *
 * Útil para registros que não possuem dados em série, como Passos ou Calorias.
 *
 * @param T O tipo da entidade de domínio principal.
 *
 * @property entity A instância da entidade de domínio mapeada.
 * @property metadata Os [HealthConnectMetadata] do registro.
 *
 * @author Nikolas Luiz Schmitt
 */
data class SingleRecordMapperResult<T: IRelationalHealthConnectEntity>(
    val entity: T,
    override val metadata: HealthConnectMetadata
) : IRecordMapperResult {

    override fun getEntityIdRelation(): List<String?> = listOf(entity.relationId)
}