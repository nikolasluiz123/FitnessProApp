package br.com.fitnesspro.health.connect.mapper.result

/**
 * Interface de contrato para resultados de mapeamento que contêm dados em série (amostras).
 *
 * Estende [IRecordMapperResult] para incluir uma lista de amostras,
 * comum em dados como frequência cardíaca ou sono.
 *
 * @param S O tipo da entidade de "amostra" (ex: [HealthConnectHeartRateSamples]).
 *
 * @property samples A lista de amostras de dados associadas ao registro principal.
 *
 * @author Nikolas Luiz Schmitt
 */
interface ISeriesRecordMapperResult<S>: IRecordMapperResult {
    val samples: List<S>
}