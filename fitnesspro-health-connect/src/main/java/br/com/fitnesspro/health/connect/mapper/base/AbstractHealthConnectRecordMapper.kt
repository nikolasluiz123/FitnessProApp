package br.com.fitnesspro.health.connect.mapper.base

import androidx.health.connect.client.records.Record
import br.com.fitnesspro.health.connect.mapper.result.IRecordMapperResult
import br.com.fitnesspro.health.connect.service.base.AbstractBaseHealthConnectService
import br.com.fitnesspro.health.connect.service.filter.RangeFilter
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata

/**
 * Classe base abstrata para mappers que leem dados do Health Connect.
 *
 * Esta classe define um padrão para mappers que consultam registros ([Record]) de um
 * [AbstractBaseHealthConnectService] específico, extraem os metadados comuns
 * e, em seguida, delegam o mapeamento específico para a implementação filha.
 *
 * @param RESULT O tipo de resultado esperado, que deve implementar [IRecordMapperResult].
 * @param RECORD O tipo de [Record] específico do Health Connect que este mapper processa (ex: [StepsRecord]).
 * @param SERVICE O tipo de [AbstractBaseHealthConnectService] usado para buscar os registros.
 *
 * @property service A instância do serviço responsável por ler os dados do Health Connect.
 *
 * @author Nikolas Luiz Schmitt
 */
abstract class AbstractHealthConnectRecordMapper<RESULT : IRecordMapperResult, RECORD : Record, SERVICE : AbstractBaseHealthConnectService<RECORD>>(
    protected val service: SERVICE
) : AbstractHealthConnectBaseMapper() {

    /**
     * Executa a leitura dos dados do Health Connect usando o [service] e o [filter] fornecido,
     * e então mapeia os resultados.
     *
     * @param filter O filtro de [RangeFilter] (data/hora inicial e final) para a consulta.
     * @return Uma lista de [RESULT] contendo os dados mapeados.
     */
    suspend fun map(filter: RangeFilter): List<RESULT> {
        val records = service.read(filter)

        return records.map { record ->
            val metadata = getMetadataFrom(record)
            continueMapping(record, metadata)
        }
    }

    /**
     * Método abstrato onde a lógica de mapeamento específica da classe filha deve ser implementada.
     * Este método é chamado após os metadados ([HealthConnectMetadata]) terem sido extraídos.
     *
     * @param record O [Record] individual retornado da consulta.
     * @param metadata Os [HealthConnectMetadata] extraídos do [record].
     * @return O objeto [RESULT] mapeado.
     */
    protected abstract suspend fun continueMapping(
        record: RECORD,
        metadata: HealthConnectMetadata
    ): RESULT
}