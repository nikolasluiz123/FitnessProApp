package br.com.fitnesspro.workout.repository.sync.importation.integration

import android.content.Context
import br.com.fitnesspro.common.injection.health.IHealthConnectIntegrationEntryPoint
import br.com.fitnesspro.common.repository.sync.importation.health.AbstractHealthConnectIntegrationRepository
import br.com.fitnesspro.health.connect.mapper.result.HeartRateMapperResult
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectMetadataDAO
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import dagger.hilt.android.EntryPointAccessors

/**
 * Implementação concreta do [AbstractHealthConnectIntegrationRepository]
 * para integrar dados de Frequência Cardíaca ([br.com.fitnesspro.model.workout.health.HealthConnectHeartRate]) do Health Connect.
 *
 * Esta classe define as dependências específicas (Mapper e DAOs) e a lógica
 * para associar os dados de frequência cardíaca com as entidades de [ExerciseExecution].
 *
 * @param context O contexto da aplicação, usado para acessar o EntryPoint do Hilt.
 *
 * @see AbstractHealthConnectIntegrationRepository
 * @see br.com.fitnesspro.health.connect.mapper.HeartRateMapper
 *
 * @author Nikolas Luiz Schmitt
 */
class HeartRateIntegrationRepository(context: Context)
    : AbstractHealthConnectIntegrationRepository<ExerciseExecution, HeartRateMapperResult>(context) {

    private val entryPoint: IHealthConnectIntegrationEntryPoint = EntryPointAccessors.fromApplication(context, IHealthConnectIntegrationEntryPoint::class.java)

    override val mapper = entryPoint.getHeartRateMapper()

    override fun getAssociationEntityDao(): IntegratedMaintenanceDAO<ExerciseExecution> {
        return entryPoint.getExerciseExecutionDAO()
    }

    override fun getMetadataDao(): HealthConnectMetadataDAO {
        return entryPoint.getHealthConnectMetadataDAO()
    }

    override suspend fun getAssociationEntities(personId: String): List<ExerciseExecution> {
        return entryPoint.getExerciseExecutionDAO().getIntegrationData(personId)
    }

    override suspend fun saveSpecificHealthData(results: List<HeartRateMapperResult>) {
        val sessionDAO = entryPoint.getHealthConnectHeartRateDAO()
        val samplesDAO = entryPoint.getHealthConnectHeartRateSamplesDAO()

        val sessions = segregate(
            list = results,
            hasEntityWithId = { sessionDAO.hasEntityWithId(it.session.id) },
            getEntity = { it.session }
        )

        val samples = segregate(
            list = results.flatMap { it.samples },
            hasEntityWithId = { samplesDAO.hasEntityWithId(it.id) },
            getEntity = { it }
        )

        saveSegregatedResult(sessions, sessionDAO)
        saveSegregatedResult(samples, samplesDAO)
    }
}