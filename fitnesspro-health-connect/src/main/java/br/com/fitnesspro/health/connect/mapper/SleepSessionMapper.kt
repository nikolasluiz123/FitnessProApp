package br.com.fitnesspro.health.connect.mapper

import androidx.health.connect.client.records.SleepSessionRecord
import br.com.android.health.connect.toolkit.mapper.AbstractHealthDataAssociatingMapper
import br.com.android.room.toolkit.model.health.interfaces.IHealthConnectMetadata
import br.com.android.room.toolkit.model.health.interfaces.IHealthDataRangeEntity
import br.com.fitnesspro.health.connect.mapper.result.SleepSessionMapperResult
import br.com.fitnesspro.health.connect.service.SleepSessionService
import br.com.fitnesspro.model.enums.health.EnumSleepStage
import br.com.fitnesspro.model.workout.health.HealthConnectSleepSession
import br.com.fitnesspro.model.workout.health.HealthConnectSleepStages
import br.com.fitnesspro.model.workout.health.SleepSessionExerciseExecution
import java.time.temporal.ChronoUnit

/**
 * Mapper específico para converter dados de [SleepSessionRecord] do Health Connect
 * em entidades de domínio [HealthConnectSleepSession] e seus [HealthConnectSleepStages].
 *
 * Este mapper possui uma lógica de associação personalizada: ele vincula a sessão de sono
 * com execuções de exercício ([IHealthDataRangeEntity]) que ocorreram *após* o término
 * do sono, dentro de uma janela de 24 horas.
 *
 * @param service A instância de [SleepSessionService] usada para buscar os dados.
 *
 * @see AbstractHealthDataAssociatingMapper
 * @see SleepSessionMapperResult
 *
 * @author Nikolas Luiz Schmitt
 */
class SleepSessionMapper(
    service: SleepSessionService
) : AbstractFitnessProHealthDataAssociatingMapper<SleepSessionMapperResult, SleepSessionRecord, SleepSessionService>(service) {

    /**
     * Mapeia um [SleepSessionRecord] para [HealthConnectSleepSession], [HealthConnectSleepStages]
     * e cria as associações personalizadas ([SleepSessionExerciseExecution]).
     *
     * @param record O registro de [SleepSessionRecord] lido.
     * @param metadata Os [HealthConnectMetadata] extraídos.
     * @param associationEntities A lista de entidades (ex: execuções de exercício)
     * com as quais este registro pode ser associado.
     * @return Um [SleepSessionMapperResult] contendo a sessão, estágios, associações e metadados.
     */
    override suspend fun <T : IHealthDataRangeEntity> continueMappingAndAssociate(
        record: SleepSessionRecord,
        metadata: IHealthConnectMetadata,
        associationEntities: List<T>
    ): SleepSessionMapperResult? {

        val session = HealthConnectSleepSession(
            healthConnectMetadataId = metadata.id,
            startTime = record.startTime,
            endTime = record.endTime,
            title = record.title,
            notes = record.notes
        )

        val associations = createSleepAssociations(session, associationEntities)
        if (associations.isEmpty()) return null

        val stages = record.stages.mapNotNull { stage ->
            mapSleepStage(stage.stage)?.let { mappedStage ->
                HealthConnectSleepStages(
                    healthConnectSleepSessionId = session.id,
                    startTime = stage.startTime,
                    endTime = stage.endTime,
                    stage = mappedStage
                )
            }
        }

        return SleepSessionMapperResult(session, stages, associations, metadata)
    }

    /**
     * Cria as associações entre a sessão de sono e as execuções de exercício.
     * A lógica filtra por execuções que iniciaram *após* o término do sono
     * e dentro de uma janela de 24 horas.
     */
    private fun <T : IHealthDataRangeEntity> createSleepAssociations(
        sleepSession: HealthConnectSleepSession,
        entities: List<T>
    ): List<SleepSessionExerciseExecution> {

        val sleepEndTime = sleepSession.endTime ?: return emptyList()

        val entitiesForTheDay = entities.filter { entity ->
            val entityStartTime = entity.rangeStartTime
            entityStartTime.isAfter(sleepEndTime) && entityStartTime.isBefore(sleepEndTime.plus(24, ChronoUnit.HOURS))
        }

        return entitiesForTheDay.map { exec ->
            SleepSessionExerciseExecution(
                healthConnectSleepSessionId = sleepSession.id,
                exerciseExecutionId = exec.id
            )
        }
    }

    /**
     * Mapeia os tipos de estágio de sono (inteiros) do [SleepSessionRecord]
     * para o enum de domínio [EnumSleepStage].
     */
    private fun mapSleepStage(stageType: Int): EnumSleepStage? {
        return when (stageType) {
            SleepSessionRecord.STAGE_TYPE_AWAKE -> EnumSleepStage.AWAKE
            SleepSessionRecord.STAGE_TYPE_SLEEPING -> EnumSleepStage.SLEEPING
            SleepSessionRecord.STAGE_TYPE_OUT_OF_BED -> EnumSleepStage.OUT_OF_BED
            SleepSessionRecord.STAGE_TYPE_LIGHT -> EnumSleepStage.LIGHT
            SleepSessionRecord.STAGE_TYPE_DEEP -> EnumSleepStage.DEEP
            SleepSessionRecord.STAGE_TYPE_REM -> EnumSleepStage.REM
            SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED -> EnumSleepStage.AWAKE_IN_BED
            else -> EnumSleepStage.UNKNOWN
        }
    }
}