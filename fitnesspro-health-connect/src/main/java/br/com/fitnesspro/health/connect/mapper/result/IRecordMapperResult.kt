package br.com.fitnesspro.health.connect.mapper.result

import br.com.fitnesspro.model.workout.health.HealthConnectMetadata

/**
 * Interface de contrato para todos os resultados de mapeamento do Health Connect.
 *
 * Garante que todo objeto de resultado gerado por um mapper contenha, no mínimo,
 * os metadados da fonte de dados.
 *
 * @property metadata Os [HealthConnectMetadata] associados ao registro mapeado.
 *
 * @author Nikolas Luiz Schmitt
 */
interface IRecordMapperResult {
    val metadata: HealthConnectMetadata
}