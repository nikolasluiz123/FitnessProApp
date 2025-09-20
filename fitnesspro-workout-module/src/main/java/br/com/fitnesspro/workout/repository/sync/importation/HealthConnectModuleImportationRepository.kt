package br.com.fitnesspro.workout.repository.sync.importation

import android.content.Context
import br.com.fitnesspro.common.injection.health.IHealthConnectModuleSyncRepositoryEntryPoint
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.common.ImportSegregationResult
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.mappers.getHealthConnectCaloriesBurned
import br.com.fitnesspro.mappers.getHealthConnectHeartRate
import br.com.fitnesspro.mappers.getHealthConnectHeartRateSamples
import br.com.fitnesspro.mappers.getHealthConnectMetadata
import br.com.fitnesspro.mappers.getHealthConnectSleepSession
import br.com.fitnesspro.mappers.getHealthConnectSleepStages
import br.com.fitnesspro.mappers.getHealthConnectSteps
import br.com.fitnesspro.mappers.getSleepSessionExerciseExecution
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.workout.health.HealthConnectCaloriesBurned
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRate
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRateSamples
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata
import br.com.fitnesspro.model.workout.health.HealthConnectSleepSession
import br.com.fitnesspro.model.workout.health.HealthConnectSleepStages
import br.com.fitnesspro.model.workout.health.HealthConnectSteps
import br.com.fitnesspro.model.workout.health.SleepSessionExerciseExecution
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.dtos.sync.HealthConnectModuleSyncDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectCaloriesBurnedDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectHeartRateDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectHeartRateSamplesDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectMetadataDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectSleepSessionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectSleepStagesDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectStepsDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.ISleepSessionExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.WorkoutModuleImportationFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDateTime
import kotlin.reflect.KClass

/**
 * Repositório de importação (pull) de dados para o Módulo Health Connect.
 *
 * Esta classe implementa o [AbstractImportationRepository] para
 * buscar, segregar e persistir todas as entidades relacionadas
 * aos dados de saúde (como [HealthConnectMetadata], [HealthConnectSteps], etc.)
 * a partir do DTO [HealthConnectModuleSyncDTO].
 *
 * @param context O contexto da aplicação.
 * @param personRepository Repositório usado para obter o `personId`
 * para o filtro de importação.
 *
 * @see AbstractImportationRepository
 * @see HealthConnectModuleSyncDTO
 *
 * @author Nikolas Luiz Schmitt
 */
class HealthConnectModuleImportationRepository(
    context: Context,
    private val personRepository: PersonRepository
) : AbstractImportationRepository<HealthConnectModuleSyncDTO, WorkoutModuleImportationFilter>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IHealthConnectModuleSyncRepositoryEntryPoint::class.java)

    /**
     * Busca os dados de sincronização do módulo Health Connect do WebClient.
     */
    override suspend fun getImportationData(
        token: String,
        filter: WorkoutModuleImportationFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<HealthConnectModuleSyncDTO> {
        return entryPoint.getHealthConnectSyncWebClient().import(token, filter, pageInfos)
    }

    /**
     * Cria o filtro [WorkoutModuleImportationFilter] necessário
     * para a API de importação, usando o ID da pessoa logada.
     */
    override suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): WorkoutModuleImportationFilter {
        val person = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!)
        return WorkoutModuleImportationFilter(lastUpdateDate = lastUpdateDate, personId = person.id)
    }

    /**
     * Implementa a segregação para todas as entidades dentro do
     * [HealthConnectModuleSyncDTO].
     *
     * Ele percorre cada lista no DTO (metadata, steps, heartRateSessions, etc.)
     * e as separa em [ImportSegregationResult] (novos vs. atualizados).
     */
    override suspend fun executeSegregation(dto: HealthConnectModuleSyncDTO): List<ImportSegregationResult<BaseModel>> {
        val result = mutableListOf<ImportSegregationResult<BaseModel>>()

        segregate(
            dtoList = dto.metadata,
            hasEntityWithId = entryPoint.getHealthConnectMetadataDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.steps,
            hasEntityWithId = entryPoint.getHealthConnectStepsDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.caloriesBurned,
            hasEntityWithId = entryPoint.getHealthConnectCaloriesBurnedDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.heartRateSessions,
            hasEntityWithId = entryPoint.getHealthConnectHeartRateDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.heartRateSamples,
            hasEntityWithId = entryPoint.getHealthConnectHeartRateSamplesDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.sleepSessions,
            hasEntityWithId = entryPoint.getHealthConnectSleepSessionDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.sleepStages,
            hasEntityWithId = entryPoint.getHealthConnectSleepStagesDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.sleepSessionAssociations,
            hasEntityWithId = entryPoint.getSleepSessionExerciseExecutionDAO()::hasEntityWithId
        )?.let(result::add)

        return result
    }

    /**
     * Converte um [BaseDTO] específico do módulo Health Connect para
     * sua entidade [BaseModel] correspondente usando os mappers.
     *
     * @throws IllegalArgumentException Se o DTO não for reconhecido.
     */
    override fun convertDTOToEntity(dto: BaseDTO): BaseModel {
        return when (dto) {
            is IHealthConnectMetadataDTO -> dto.getHealthConnectMetadata()
            is IHealthConnectStepsDTO -> dto.getHealthConnectSteps()
            is IHealthConnectCaloriesBurnedDTO -> dto.getHealthConnectCaloriesBurned()
            is IHealthConnectHeartRateDTO -> dto.getHealthConnectHeartRate()
            is IHealthConnectHeartRateSamplesDTO -> dto.getHealthConnectHeartRateSamples()
            is IHealthConnectSleepSessionDTO -> dto.getHealthConnectSleepSession()
            is IHealthConnectSleepStagesDTO -> dto.getHealthConnectSleepStages()
            is ISleepSessionExerciseExecutionDTO -> dto.getSleepSessionExerciseExecution()
            else -> throw IllegalArgumentException("Não foi possível converter o DTO Health. Classe de modelo inválida: ${dto::class.java.name}")
        }
    }

    /**
     * Retorna o [MaintenanceDAO] apropriado para uma determinada
     * [KClass] de entidade do módulo Health Connect.
     *
     * @throws IllegalArgumentException Se a classe de modelo não for reconhecida.
     */
    override fun getMaintenanceDAO(modelClass: KClass<out BaseModel>): MaintenanceDAO<out BaseModel> {
        return when (modelClass) {
            HealthConnectMetadata::class -> entryPoint.getHealthConnectMetadataDAO()
            HealthConnectSteps::class -> entryPoint.getHealthConnectStepsDAO()
            HealthConnectCaloriesBurned::class -> entryPoint.getHealthConnectCaloriesBurnedDAO()
            HealthConnectHeartRate::class -> entryPoint.getHealthConnectHeartRateDAO()
            HealthConnectHeartRateSamples::class -> entryPoint.getHealthConnectHeartRateSamplesDAO()
            HealthConnectSleepSession::class -> entryPoint.getHealthConnectSleepSessionDAO()
            HealthConnectSleepStages::class -> entryPoint.getHealthConnectSleepStagesDAO()
            SleepSessionExerciseExecution::class -> entryPoint.getSleepSessionExerciseExecutionDAO()
            else -> throw IllegalArgumentException("Não foi possível recuperar o DAO Health. Classe de modelo inválida: ${modelClass.simpleName}")
        }
    }
}