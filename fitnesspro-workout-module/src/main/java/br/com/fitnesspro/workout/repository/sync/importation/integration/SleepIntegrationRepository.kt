package br.com.fitnesspro.workout.repository.sync.importation.integration

import android.content.Context
import br.com.fitnesspro.common.injection.health.IHealthConnectIntegrationEntryPoint
import br.com.fitnesspro.common.repository.sync.importation.health.AbstractHealthConnectIntegrationRepository
import br.com.fitnesspro.health.connect.mapper.result.SleepSessionMapperResult
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectMetadataDAO
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import dagger.hilt.android.EntryPointAccessors

class SleepIntegrationRepository(context: Context)
    : AbstractHealthConnectIntegrationRepository<ExerciseExecution, SleepSessionMapperResult>(context) {

    private val entryPoint: IHealthConnectIntegrationEntryPoint =
        EntryPointAccessors.fromApplication(context, IHealthConnectIntegrationEntryPoint::class.java)

    override val mapper = entryPoint.getSleepSessionMapper()

    override fun getAssociationEntityDao(): IntegratedMaintenanceDAO<ExerciseExecution> {
        return entryPoint.getExerciseExecutionDAO()
    }

    override fun getMetadataDao(): HealthConnectMetadataDAO {
        return entryPoint.getHealthConnectMetadataDAO()
    }

    override suspend fun getAssociationEntities(personId: String): List<ExerciseExecution> {
        return entryPoint.getExerciseExecutionDAO().getIntegrationData(personId)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun saveSpecificHealthData(results: List<SleepSessionMapperResult>) {
        val healthConnectSleepSessionDAO = entryPoint.getHealthConnectSleepSessionDAO()
        val healthConnectSleepStagesDAO = entryPoint.getHealthConnectSleepStagesDAO()
        val sleepSessionExerciseExecutionDAO = entryPoint.getSleepSessionExerciseExecutionDAO()

        val sessionSegregationResult = segregate(
            list = results,
            hasEntityWithId = { healthConnectSleepSessionDAO.hasEntityWithId(it.session.id) },
            getEntity = { it.session }
        )

        val stagesSegregationResult = segregate(
            list = results.flatMap { it.stages },
            hasEntityWithId = { healthConnectSleepStagesDAO.hasEntityWithId(it.id) },
            getEntity = { it }
        )

        val associationsSegregationResult = segregate(
            list = results.flatMap { it.associations },
            hasEntityWithId = { sleepSessionExerciseExecutionDAO.hasEntityWithId(it.id) },
            getEntity = { it }
        )

        saveSegregatedResult(sessionSegregationResult, healthConnectSleepSessionDAO)
        saveSegregatedResult(stagesSegregationResult, healthConnectSleepStagesDAO)
        saveSegregatedResult(associationsSegregationResult, sleepSessionExerciseExecutionDAO)
    }
}