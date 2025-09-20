package br.com.fitnesspro.workout.repository.sync.importation.integration

import android.content.Context
import br.com.fitnesspro.common.injection.health.IHealthConnectIntegrationEntryPoint
import br.com.fitnesspro.common.repository.sync.importation.health.AbstractHealthConnectIntegrationRepository
import br.com.fitnesspro.health.connect.mapper.result.SingleRecordMapperResult
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectMetadataDAO
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import br.com.fitnesspro.model.workout.health.HealthConnectSteps
import dagger.hilt.android.EntryPointAccessors

/**
 * Implementação concreta do [AbstractHealthConnectIntegrationRepository]
 * para integrar dados de Passos ([HealthConnectSteps]) do Health Connect.
 *
 * Esta classe define as dependências específicas (Mapper e DAOs) e a lógica
 * para associar os dados de passos com as entidades de [ExerciseExecution].
 *
 * @param context O contexto da aplicação, usado para acessar o EntryPoint do Hilt.
 *
 * @see AbstractHealthConnectIntegrationRepository
 * @see br.com.fitnesspro.health.connect.mapper.StepsMapper
 *
 * @author Nikolas Luiz Schmitt
 */
class StepsIntegrationRepository(context: Context)
    : AbstractHealthConnectIntegrationRepository<ExerciseExecution, SingleRecordMapperResult<HealthConnectSteps>>(context) {

    private val entryPoint: IHealthConnectIntegrationEntryPoint =
        EntryPointAccessors.fromApplication(context, IHealthConnectIntegrationEntryPoint::class.java)

    override val mapper = entryPoint.getStepsMapper()

    override fun getAssociationEntityDao(): IntegratedMaintenanceDAO<ExerciseExecution> {
        return entryPoint.getExerciseExecutionDAO()
    }

    override fun getMetadataDao(): HealthConnectMetadataDAO {
        return entryPoint.getHealthConnectMetadataDAO()
    }

    override suspend fun getAssociationEntities(personId: String): List<ExerciseExecution> {
        return entryPoint.getExerciseExecutionDAO().getIntegrationData(personId)
    }

    override suspend fun saveSpecificHealthData(results: List<SingleRecordMapperResult<HealthConnectSteps>>) {
        val dao = entryPoint.getHealthConnectStepsDAO()

        val steps = segregate(
            list = results,
            hasEntityWithId = { dao.hasEntityWithId(it.entity.id) },
            getEntity = { it.entity }
        )

        saveSegregatedResult(steps, dao)
    }
}