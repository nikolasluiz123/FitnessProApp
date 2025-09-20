package br.com.fitnesspro.workout.repository.sync.importation.integration

import android.content.Context
import br.com.fitnesspro.common.injection.health.IHealthConnectIntegrationEntryPoint
import br.com.fitnesspro.common.repository.sync.importation.health.AbstractHealthConnectIntegrationRepository
import br.com.fitnesspro.health.connect.mapper.result.SingleRecordMapperResult
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectMetadataDAO
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import br.com.fitnesspro.model.workout.health.HealthConnectCaloriesBurned
import dagger.hilt.android.EntryPointAccessors

class CaloriesIntegrationRepository(context: Context)
    : AbstractHealthConnectIntegrationRepository<ExerciseExecution, SingleRecordMapperResult<HealthConnectCaloriesBurned>>(context) {

    private val entryPoint: IHealthConnectIntegrationEntryPoint =
        EntryPointAccessors.fromApplication(context, IHealthConnectIntegrationEntryPoint::class.java)

    override val mapper = entryPoint.getCaloriesBurnedMapper()

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
    override suspend fun saveSpecificHealthData(results: List<SingleRecordMapperResult<HealthConnectCaloriesBurned>>) {
        val dao = entryPoint.getHealthConnectCaloriesBurnedDAO()
        val segregationResult = segregate(
            list = results,
            hasEntityWithId = { dao.hasEntityWithId(it.entity.id) },
            getEntity = { it.entity }
        )

        saveSegregatedResult(segregationResult, dao)
    }
}