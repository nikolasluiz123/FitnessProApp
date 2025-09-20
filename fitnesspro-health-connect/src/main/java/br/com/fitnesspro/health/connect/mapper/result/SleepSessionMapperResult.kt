package br.com.fitnesspro.health.connect.mapper.result

import br.com.fitnesspro.model.workout.health.HealthConnectMetadata
import br.com.fitnesspro.model.workout.health.HealthConnectSleepSession
import br.com.fitnesspro.model.workout.health.HealthConnectSleepStages
import br.com.fitnesspro.model.workout.health.SleepSessionExerciseExecution

/**
 * Data class que encapsula o resultado completo do mapeamento de uma sessão de sono.
 *
 * Implementa [IRecordMapperResult] para incluir os metadados.
 *
 * @property session A entidade [HealthConnectSleepSession] mapeada.
 * @property stages A lista de [HealthConnectSleepStages] (estágios do sono) associados à sessão.
 * @property associations A lista de [SleepSessionExerciseExecution] (associações com exercícios)
 * criadas para esta sessão.
 * @property metadata Os [HealthConnectMetadata] da sessão.
 *
 * @author Nikolas Luiz Schmitt
 */
data class SleepSessionMapperResult(
    val session: HealthConnectSleepSession,
    val stages: List<HealthConnectSleepStages>,
    val associations: List<SleepSessionExerciseExecution>,
    override val metadata: HealthConnectMetadata
) : IRecordMapperResult