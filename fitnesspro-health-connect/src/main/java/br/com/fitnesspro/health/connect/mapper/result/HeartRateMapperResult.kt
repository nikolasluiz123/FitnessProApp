package br.com.fitnesspro.health.connect.mapper.result

import br.com.fitnesspro.model.workout.health.HealthConnectHeartRate
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRateSamples
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata

/**
 * Data class que encapsula o resultado completo do mapeamento de uma sessão
 * de frequência cardíaca.
 *
 * Implementa [ISeriesRecordMapperResult] para incluir a lista de amostras.
 *
 * @property session A entidade [HealthConnectHeartRate] (sessão) mapeada.
 * @property samples A lista de [HealthConnectHeartRateSamples] (amostras de BPM)
 * associadas à sessão.
 * @property metadata Os [HealthConnectMetadata] da sessão.
 *
 * @author Nikolas Luiz Schmitt
 */
data class HeartRateMapperResult(
    val session: HealthConnectHeartRate,
    override val samples: List<HealthConnectHeartRateSamples>,
    override val metadata: HealthConnectMetadata
) : ISeriesRecordMapperResult<HealthConnectHeartRateSamples>