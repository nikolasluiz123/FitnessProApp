package br.com.fitnesspro.workout.repository.sync.exportation

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.mappers.getHealthConnectCaloriesBurnedDTO
import br.com.fitnesspro.mappers.getHealthConnectHeartRateDTO
import br.com.fitnesspro.mappers.getHealthConnectHeartRateSamplesDTO
import br.com.fitnesspro.mappers.getHealthConnectMetadataDTO
import br.com.fitnesspro.mappers.getHealthConnectSleepSessionDTO
import br.com.fitnesspro.mappers.getHealthConnectSleepStagesDTO
import br.com.fitnesspro.mappers.getHealthConnectStepsDTO
import br.com.fitnesspro.mappers.getSleepSessionExerciseExecutionDTO
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.workout.health.HealthConnectCaloriesBurned
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRate
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRateSamples
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata
import br.com.fitnesspro.model.workout.health.HealthConnectSleepSession
import br.com.fitnesspro.model.workout.health.HealthConnectSleepStages
import br.com.fitnesspro.model.workout.health.HealthConnectSteps
import br.com.fitnesspro.model.workout.health.SleepSessionExerciseExecution
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.common.injection.health.IHealthConnectModuleSyncRepositoryEntryPoint
import br.com.fitnesspro.shared.communication.dtos.sync.HealthConnectModuleSyncDTO
import dagger.hilt.android.EntryPointAccessors
import kotlin.reflect.KClass

class HealthConnectModuleExportationRepository(
    context: Context,
    private val personRepository: PersonRepository
) : AbstractExportationRepository<HealthConnectModuleSyncDTO>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IHealthConnectModuleSyncRepositoryEntryPoint::class.java)

    override suspend fun getExportationData(pageInfos: ExportPageInfos): Map<KClass<out IntegratedModel>, List<IntegratedModel>> {
        val map = mutableMapOf<KClass<out IntegratedModel>, List<IntegratedModel>>()
        val personId = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!).id

        map.put(HealthConnectMetadata::class, entryPoint.getHealthConnectMetadataDAO().getExportationData(pageInfos, personId))
        map.put(HealthConnectSteps::class, entryPoint.getHealthConnectStepsDAO().getExportationData(pageInfos, personId))
        map.put(HealthConnectCaloriesBurned::class, entryPoint.getHealthConnectCaloriesBurnedDAO().getExportationData(pageInfos, personId))
        map.put(HealthConnectHeartRate::class, entryPoint.getHealthConnectHeartRateDAO().getExportationData(pageInfos, personId))
        map.put(HealthConnectHeartRateSamples::class, entryPoint.getHealthConnectHeartRateSamplesDAO().getExportationData(pageInfos, personId))
        map.put(HealthConnectSleepSession::class, entryPoint.getHealthConnectSleepSessionDAO().getExportationData(pageInfos, personId))
        map.put(HealthConnectSleepStages::class, entryPoint.getHealthConnectSleepStagesDAO().getExportationData(pageInfos, personId))
        map.put(SleepSessionExerciseExecution::class, entryPoint.getSleepSessionExerciseExecutionDAO().getExportationData(pageInfos, personId))

        return map
    }

    override suspend fun getExportationDTO(models: Map<KClass<out IntegratedModel>, List<IntegratedModel>>): HealthConnectModuleSyncDTO {

        val metadata = models[HealthConnectMetadata::class]!!.map { (it as HealthConnectMetadata).getHealthConnectMetadataDTO() }
        val steps = models[HealthConnectSteps::class]!!.map { (it as HealthConnectSteps).getHealthConnectStepsDTO() }
        val calories = models[HealthConnectCaloriesBurned::class]!!.map { (it as HealthConnectCaloriesBurned).getHealthConnectCaloriesBurnedDTO() }
        val hrSessions = models[HealthConnectHeartRate::class]!!.map { (it as HealthConnectHeartRate).getHealthConnectHeartRateDTO() }
        val hrSamples = models[HealthConnectHeartRateSamples::class]!!.map { (it as HealthConnectHeartRateSamples).getHealthConnectHeartRateSamplesDTO() }
        val sleepSessions = models[HealthConnectSleepSession::class]!!.map { (it as HealthConnectSleepSession).getHealthConnectSleepSessionDTO() }
        val sleepStages = models[HealthConnectSleepStages::class]!!.map { (it as HealthConnectSleepStages).getHealthConnectSleepStagesDTO() }
        val sleepAssocs = models[SleepSessionExerciseExecution::class]!!.map { (it as SleepSessionExerciseExecution).getSleepSessionExerciseExecutionDTO() }

        return HealthConnectModuleSyncDTO(
            metadata = metadata,
            steps = steps,
            caloriesBurned = calories,
            heartRateSessions = hrSessions,
            heartRateSamples = hrSamples,
            sleepSessions = sleepSessions,
            sleepStages = sleepStages,
            sleepSessionAssociations = sleepAssocs
        )
    }

    override suspend fun callExportationService(
        dto: HealthConnectModuleSyncDTO,
        token: String
    ): ExportationServiceResponse {
        return entryPoint.getHealthConnectSyncWebClient().export(token, dto)
    }

    override fun getIntegratedMaintenanceDAO(modelClass: KClass<out IntegratedModel>): IntegratedMaintenanceDAO<out IntegratedModel> {
        return when (modelClass) {
            HealthConnectMetadata::class -> entryPoint.getHealthConnectMetadataDAO()
            HealthConnectSteps::class -> entryPoint.getHealthConnectStepsDAO()
            HealthConnectCaloriesBurned::class -> entryPoint.getHealthConnectCaloriesBurnedDAO()
            HealthConnectHeartRate::class -> entryPoint.getHealthConnectHeartRateDAO()
            HealthConnectHeartRateSamples::class -> entryPoint.getHealthConnectHeartRateSamplesDAO()
            HealthConnectSleepSession::class -> entryPoint.getHealthConnectSleepSessionDAO()
            HealthConnectSleepStages::class -> entryPoint.getHealthConnectSleepStagesDAO()
            SleepSessionExerciseExecution::class -> entryPoint.getSleepSessionExerciseExecutionDAO()
            else -> throw IllegalArgumentException("Não foi possível recuperar o DAO Health (Export). Classe de modelo inválida: ${modelClass.simpleName}")
        }
    }
}