package br.com.fitnesspro.common.repository.sync.importation.health

import android.content.Context
import android.util.Log
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.common.repository.sync.importation.common.ImportSegregationResult
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE_TIME_SHORT
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.worker.LogConstants
import br.com.fitnesspro.core.worker.LogConstants.WORKER_IMPORT
import br.com.fitnesspro.health.connect.mapper.base.AbstractHealthDataAssociatingMapper
import br.com.fitnesspro.health.connect.mapper.result.IRecordMapperResult
import br.com.fitnesspro.health.connect.service.filter.RangeFilter
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectMetadataDAO
import br.com.fitnesspro.model.base.IHealthDataRangeEntity
import br.com.fitnesspro.model.base.IntegratedModel
import java.time.Instant

/**
 * Define o padrão de repositório (Template Method) para a integração de dados
 * do Health Connect com o banco de dados local.
 *
 * Esta classe orquestra o processo completo de importação de dados de saúde:
 * 1. Busca entidades locais que precisam de dados de saúde (ex: [ExerciseExecution]).
 * 2. Cria um [RangeFilter] com base no tempo dessas entidades.
 * 3. Utiliza um [AbstractHealthDataAssociatingMapper] para buscar dados do Health Connect
 * e associá-los às entidades locais.
 * 4. Salva os resultados (Metadados e dados específicos).
 * 5. Marca as entidades locais como "coletadas".
 *
 * As classes filhas devem implementar os métodos abstratos para fornecer
 * os DAOs e Mappers específicos para cada tipo de dado (Passos, Calorias, etc.).
 *
 * @param ENTITY A entidade de domínio local que implementa [IHealthDataRangeEntity]
 * (ex: `ExerciseExecution`), à qual os dados de saúde serão associados.
 * @param RESULT O tipo de resultado esperado do mapper, que implementa [IRecordMapperResult].
 *
 * @see AbstractHealthDataAssociatingMapper
 *
 * @author Nikolas Luiz Schmitt
 */
abstract class AbstractHealthConnectIntegrationRepository<ENTITY : IHealthDataRangeEntity, RESULT : IRecordMapperResult>(
    context: Context
) : FitnessProRepository(context) {

    /**
     * Propriedade abstrata que deve ser implementada para fornecer o mapper
     * específico (ex: [StepsMapper], [HeartRateMapper]) para este tipo de dado.
     * O mapper deve ser do tipo [AbstractHealthDataAssociatingMapper].
     */
    protected abstract val mapper: AbstractHealthDataAssociatingMapper<RESULT, *, *>

    /**
     * Método abstrato que deve ser implementado para fornecer o DAO da
     * entidade de associação [ENTITY].
     * (ex: `ExerciseExecutionDAO`).
     */
    protected abstract fun getAssociationEntityDao(): IntegratedMaintenanceDAO<ENTITY>

    /**
     * Método abstrato que deve ser implementado para fornecer o DAO
     * [HealthConnectMetadataDAO], usado para salvar os metadados.
     */
    protected abstract fun getMetadataDao(): HealthConnectMetadataDAO

    /**
     * Método abstrato para implementar a lógica de persistência dos dados de saúde
     * específicos contidos nos [results].
     * (ex: salvar `HealthConnectHeartRate` e `HealthConnectHeartRateSamples`).
     *
     * @param results A lista de resultados mapeados.
     */
    protected abstract suspend fun saveSpecificHealthData(results: List<RESULT>)

    /**
     * Método abstrato para buscar as entidades locais ([ENTITY]) que
     * necessitam da coleta de dados de saúde.
     * (ex: buscar execuções de exercício onde `healthDataCollected = false`).
     *
     * @param personId O ID da pessoa logada.
     * @return Uma lista de entidades [ENTITY] para associação.
     */
    protected abstract suspend fun getAssociationEntities(personId: String): List<ENTITY>

    /**
     * Executa o fluxo principal de integração de dados do Health Connect.
     * Este método orquestra a chamada aos métodos abstratos na ordem correta.
     *
     * @param personId O ID da pessoa para a qual a integração será executada.
     */
    suspend fun runIntegration(personId: String) {
        Log.i(LogConstants.WORKER_IMPORT, "Realizando Integração ${javaClass.simpleName}")

        val entitiesToAssociate = getAssociationEntities(personId)
        if (entitiesToAssociate.isEmpty()) {
            return
        }

        val filter = createRangeFilterFromEntities(entitiesToAssociate) ?: return
        val mappedResults = mapper.mapAndAssociate(filter, entitiesToAssociate)

        if (mappedResults.isEmpty()) {
            return
        }

        Log.i(LogConstants.WORKER_IMPORT, "Importou ${mappedResults.size} registros")

        saveResults(mappedResults)
        markEntitiesAsCollected(entitiesToAssociate)
    }

    protected suspend fun <T, M : IntegratedModel> segregate(
        list: List<T>,
        hasEntityWithId: suspend (T) -> Boolean,
        getEntity: (T) -> M
    ): ImportSegregationResult<M>? {
        return if (list.isNotEmpty()) {
            val insertionList = list
                .filter { !hasEntityWithId(it) }
                .map { getEntity(it) }

            val updateList = list
                .filter { hasEntityWithId(it) }
                .map { getEntity(it) }

            ImportSegregationResult(insertionList, updateList)
        } else {
            null
        }
    }

    protected suspend fun <M : IntegratedModel> saveSegregatedResult(
        result: ImportSegregationResult<M>?,
        dao: IntegratedMaintenanceDAO<M>
    ) {
        if (result?.insertionList?.isNotEmpty() == true) {
            dao.insertBatch(result.insertionList)
        }

        if (result?.updateList?.isNotEmpty() == true) {
            dao.updateBatch(result.updateList)
        }
    }

    /**
     * Salva os resultados mapeados no banco de dados.
     * Primeiro, salva os metadados comuns e, em seguida, chama
     * [saveSpecificHealthData] para salvar os dados específicos.
     */
    private suspend fun saveResults(results: List<RESULT>) {
        val segregationResult = segregate(
            list = results,
            hasEntityWithId = { getMetadataDao().hasEntityWithId(it.metadata.id) },
            getEntity = { it.metadata }
        )

        if (segregationResult?.insertionList?.isNotEmpty() == true) {
            getMetadataDao().insertBatch(segregationResult.insertionList)
        }

        if (segregationResult?.updateList?.isNotEmpty() == true) {
            getMetadataDao().updateBatch(segregationResult.updateList)
        }

        saveSpecificHealthData(results)
    }

    /**
     * Atualiza as entidades de associação (ex: `ExerciseExecution`) no banco de dados,
     * marcando o campo `healthDataCollected` como `true`.
     */
    private suspend fun markEntitiesAsCollected(entities: List<ENTITY>) {
        entities.forEach { it.healthDataCollected = true }
        getAssociationEntityDao().updateBatch(entities, writeTransmissionState = true)
    }

    /**
     * Cria um [RangeFilter] abrangente que engloba o tempo de início mínimo
     * e o tempo de término máximo de todas as entidades de associação.
     */
    private fun createRangeFilterFromEntities(entities: List<ENTITY>): RangeFilter? {
        if (entities.isEmpty()) return null
        val minStart = entities.minOf { it.rangeStartTime }
        val maxEnd = entities.mapNotNull { it.rangeEndTime }.maxOrNull() ?: Instant.now()

        if (minStart.isAfter(maxEnd)) return null

        Log.i(WORKER_IMPORT, "RangeFilter start = ${minStart.format(DATE_TIME_SHORT)} end = ${maxEnd.format(DATE_TIME_SHORT)}")

        return RangeFilter(minStart, maxEnd)
    }
}