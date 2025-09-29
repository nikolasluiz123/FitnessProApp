package br.com.fitnesspro.workout.repository.sync.importation

import android.content.Context
import br.com.android.room.toolkit.dao.MaintenanceDAO
import br.com.android.room.toolkit.model.interfaces.BaseModel
import br.com.fitnesspro.common.injection.health.IHealthConnectModuleSyncRepositoryEntryPoint
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.common.ImportSegregationResult
import br.com.fitnesspro.mappers.getExercise
import br.com.fitnesspro.mappers.getExerciseExecution
import br.com.fitnesspro.mappers.getExercisePreDefinition
import br.com.fitnesspro.mappers.getHealthConnectCaloriesBurned
import br.com.fitnesspro.mappers.getHealthConnectHeartRate
import br.com.fitnesspro.mappers.getHealthConnectHeartRateSamples
import br.com.fitnesspro.mappers.getHealthConnectMetadata
import br.com.fitnesspro.mappers.getHealthConnectSleepSession
import br.com.fitnesspro.mappers.getHealthConnectSleepStages
import br.com.fitnesspro.mappers.getHealthConnectSteps
import br.com.fitnesspro.mappers.getReport
import br.com.fitnesspro.mappers.getSleepSessionExerciseExecution
import br.com.fitnesspro.mappers.getVideo
import br.com.fitnesspro.mappers.getVideoExercise
import br.com.fitnesspro.mappers.getVideoExerciseExecution
import br.com.fitnesspro.mappers.getVideoExercisePreDefinition
import br.com.fitnesspro.mappers.getWorkout
import br.com.fitnesspro.mappers.getWorkoutGroup
import br.com.fitnesspro.mappers.getWorkoutGroupPreDefinition
import br.com.fitnesspro.mappers.getWorkoutReport
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.model.general.report.WorkoutReport
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.model.workout.VideoExercise
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution
import br.com.fitnesspro.model.workout.health.HealthConnectCaloriesBurned
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRate
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRateSamples
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata
import br.com.fitnesspro.model.workout.health.HealthConnectSleepSession
import br.com.fitnesspro.model.workout.health.HealthConnectSleepStages
import br.com.fitnesspro.model.workout.health.HealthConnectSteps
import br.com.fitnesspro.model.workout.health.SleepSessionExerciseExecution
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.VideoExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IWorkoutReportDTO
import br.com.fitnesspro.shared.communication.dtos.sync.WorkoutModuleSyncDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IExercisePreDefinitionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IVideoDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IVideoExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IVideoExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IVideoExercisePreDefinitionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IWorkoutDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IWorkoutGroupDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IWorkoutGroupPreDefinitionDTO
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
import br.com.fitnesspro.workout.injection.IWorkoutModuleSyncRepositoryEntryPoint
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDateTime
import kotlin.reflect.KClass

/**
 * Repositório de importação (pull) de dados para o Módulo de Treino.
 *
 * Esta classe implementa o [AbstractImportationRepository] para
 * buscar, segregar (separar novos de atualizados) e persistir
 * todas as entidades relacionadas ao módulo de treino
 * (como [Workout], [Exercise], [ExerciseExecution], etc.)
 * a partir do DTO [WorkoutModuleSyncDTO].
 *
 * @param context O contexto da aplicação.
 * @param personRepository Repositório usado para obter o `personId`
 * para o filtro de importação.
 *
 * @see AbstractImportationRepository
 * @see WorkoutModuleSyncDTO
 *
 * @author Nikolas Luiz Schmitt
 */
class WorkoutModuleImportationRepository(
    context: Context,
    private val personRepository: PersonRepository
): AbstractImportationRepository<WorkoutModuleSyncDTO, WorkoutModuleImportationFilter>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IWorkoutModuleSyncRepositoryEntryPoint::class.java)
    private val healthConnectEntryPoint = EntryPointAccessors.fromApplication(context, IHealthConnectModuleSyncRepositoryEntryPoint::class.java)

    override fun getPageSize(): Int = 500

    /**
     * Busca os dados de sincronização do módulo de treino do WebClient.
     */
    override suspend fun getImportationData(
        token: String,
        filter: WorkoutModuleImportationFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<WorkoutModuleSyncDTO> {
        return entryPoint.getWorkoutSyncWebClient().import(token, filter, pageInfos)
    }

    /**
     * Cria o filtro [WorkoutModuleImportationFilter] necessário
     * para a API de importação, usando o ID da pessoa logada.
     */
    override suspend fun getImportFilter(lastUpdateDateMap: MutableMap<String, LocalDateTime?>): WorkoutModuleImportationFilter {
        val person = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!)
        return WorkoutModuleImportationFilter(lastUpdateDateMap = lastUpdateDateMap, personId = person.id)
    }

    /**
     * Implementa a segregação para todas as entidades dentro do
     * [WorkoutModuleSyncDTO].
     *
     * Ele percorre cada lista no DTO (workouts, exercises, etc.)
     * e as separa em [ImportSegregationResult] (novos vs. atualizados).
     */
    override suspend fun executeSegregation(dto: WorkoutModuleSyncDTO): List<ImportSegregationResult<BaseModel>> {
        val result = mutableListOf<ImportSegregationResult<BaseModel>>()

        segregate(
            dtoList = dto.workouts,
            hasEntityWithId = entryPoint.getWorkoutDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.workoutGroups,
            hasEntityWithId = entryPoint.getWorkoutGroupDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.exercises,
            hasEntityWithId = entryPoint.getExerciseDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.videos,
            hasEntityWithId = entryPoint.getVideoDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.videoExercises,
            hasEntityWithId = entryPoint.getVideoExerciseDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.exerciseExecutions,
            hasEntityWithId = entryPoint.getExerciseExecutionDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.workoutGroupsPreDefinitions,
            hasEntityWithId = entryPoint.getWorkoutGroupPreDefinitionDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.exercisePredefinitions,
            hasEntityWithId = entryPoint.getExercisePreDefinitionDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.videoExercisePreDefinitions,
            hasEntityWithId = entryPoint.getVideoExercisePreDefinitionDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.reports,
            hasEntityWithId = entryPoint.getReportDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.workoutReports,
            hasEntityWithId = entryPoint.getWorkoutReportDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.metadata,
            hasEntityWithId = healthConnectEntryPoint.getHealthConnectMetadataDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.steps,
            hasEntityWithId = healthConnectEntryPoint.getHealthConnectStepsDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.caloriesBurned,
            hasEntityWithId = healthConnectEntryPoint.getHealthConnectCaloriesBurnedDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.heartRateSessions,
            hasEntityWithId = healthConnectEntryPoint.getHealthConnectHeartRateDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.heartRateSamples,
            hasEntityWithId = healthConnectEntryPoint.getHealthConnectHeartRateSamplesDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.sleepSessions,
            hasEntityWithId = healthConnectEntryPoint.getHealthConnectSleepSessionDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.sleepStages,
            hasEntityWithId = healthConnectEntryPoint.getHealthConnectSleepStagesDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.sleepSessionAssociations,
            hasEntityWithId = healthConnectEntryPoint.getSleepSessionExerciseExecutionDAO()::hasEntityWithId
        )?.let(result::add)

        return result
    }

    /**
     * Converte um [BaseDTO] específico do módulo de treino para
     * sua entidade [BaseModel] correspondente usando os mappers.
     *
     * @throws IllegalArgumentException Se o DTO não for reconhecido.
     */
    override fun convertDTOToEntity(dto: BaseDTO): BaseModel {
        return when (dto) {
            is IWorkoutDTO -> dto.getWorkout()
            is IWorkoutGroupDTO -> dto.getWorkoutGroup()
            is IExerciseDTO -> dto.getExercise()
            is IVideoDTO -> dto.getVideo()
            is IVideoExerciseDTO -> dto.getVideoExercise()
            is IExerciseExecutionDTO -> dto.getExerciseExecution()
            is IVideoExerciseExecutionDTO -> dto.getVideoExerciseExecution()
            is IWorkoutGroupPreDefinitionDTO -> dto.getWorkoutGroupPreDefinition()
            is IExercisePreDefinitionDTO -> dto.getExercisePreDefinition()
            is IVideoExercisePreDefinitionDTO -> dto.getVideoExercisePreDefinition()
            is IReportDTO -> dto.getReport()
            is IWorkoutReportDTO -> dto.getWorkoutReport()

            is IHealthConnectMetadataDTO -> dto.getHealthConnectMetadata()
            is IHealthConnectStepsDTO -> dto.getHealthConnectSteps()
            is IHealthConnectCaloriesBurnedDTO -> dto.getHealthConnectCaloriesBurned()
            is IHealthConnectHeartRateDTO -> dto.getHealthConnectHeartRate()
            is IHealthConnectHeartRateSamplesDTO -> dto.getHealthConnectHeartRateSamples()
            is IHealthConnectSleepSessionDTO -> dto.getHealthConnectSleepSession()
            is IHealthConnectSleepStagesDTO -> dto.getHealthConnectSleepStages()
            is ISleepSessionExerciseExecutionDTO -> dto.getSleepSessionExerciseExecution()

            else -> {
                throw IllegalArgumentException("Não foi possível converter o DTO. Classe de modelo inválida")
            }
        }
    }

    /**
     * Retorna o [MaintenanceDAO] apropriado para uma determinada
     * [KClass] de entidade do módulo de treino.
     *
     * @throws IllegalArgumentException Se a classe de modelo não for reconhecida.
     */
    override fun getMaintenanceDAO(modelClass: KClass<out BaseModel>): MaintenanceDAO<out BaseModel> {
        return when (modelClass) {
            Workout::class -> entryPoint.getWorkoutDAO()
            WorkoutGroup::class -> entryPoint.getWorkoutGroupDAO()
            Exercise::class -> entryPoint.getExerciseDAO()
            Video::class -> entryPoint.getVideoDAO()
            VideoExercise::class -> entryPoint.getVideoExerciseDAO()
            ExerciseExecution::class -> entryPoint.getExerciseExecutionDAO()
            VideoExerciseExecution::class -> entryPoint.getVideoExerciseExecutionDAO()
            WorkoutGroupPreDefinition::class -> entryPoint.getWorkoutGroupPreDefinitionDAO()
            ExercisePreDefinition::class -> entryPoint.getExercisePreDefinitionDAO()
            VideoExercisePreDefinition::class -> entryPoint.getVideoExercisePreDefinitionDAO()
            Report::class -> entryPoint.getReportDAO()
            WorkoutReport::class -> entryPoint.getWorkoutReportDAO()

            HealthConnectMetadata::class -> healthConnectEntryPoint.getHealthConnectMetadataDAO()
            HealthConnectSteps::class -> healthConnectEntryPoint.getHealthConnectStepsDAO()
            HealthConnectCaloriesBurned::class -> healthConnectEntryPoint.getHealthConnectCaloriesBurnedDAO()
            HealthConnectHeartRate::class -> healthConnectEntryPoint.getHealthConnectHeartRateDAO()
            HealthConnectHeartRateSamples::class -> healthConnectEntryPoint.getHealthConnectHeartRateSamplesDAO()
            HealthConnectSleepSession::class -> healthConnectEntryPoint.getHealthConnectSleepSessionDAO()
            HealthConnectSleepStages::class -> healthConnectEntryPoint.getHealthConnectSleepStagesDAO()
            SleepSessionExerciseExecution::class -> healthConnectEntryPoint.getSleepSessionExerciseExecutionDAO()

            else -> throw IllegalArgumentException("Não foi possível recuperar o DAO. Classe de modelo inválida.")
        }
    }

    override fun getModule() = EnumSyncModule.WORKOUT

    override fun getListImportedModelClassesNames(): List<String> {
        return listOf(
            Workout::class.simpleName!!,
            WorkoutGroup::class.simpleName!!,
            Exercise::class.simpleName!!,
            Video::class.simpleName!!,
            VideoExercise::class.simpleName!!,
            ExerciseExecution::class.simpleName!!,
            VideoExerciseExecution::class.simpleName!!,
            WorkoutGroupPreDefinition::class.simpleName!!,
            ExercisePreDefinition::class.simpleName!!,
            VideoExercisePreDefinition::class.simpleName!!,
            Report::class.simpleName!!,
            WorkoutReport::class.simpleName!!,
            HealthConnectMetadata::class.simpleName!!,
            HealthConnectSteps::class.simpleName!!,
            HealthConnectCaloriesBurned::class.simpleName!!,
            HealthConnectHeartRate::class.simpleName!!,
            HealthConnectHeartRateSamples::class.simpleName!!,
            HealthConnectSleepSession::class.simpleName!!,
            HealthConnectSleepStages::class.simpleName!!,
            SleepSessionExerciseExecution::class.simpleName!!
        )
    }

    override fun getCursorDataFrom(syncDTO: WorkoutModuleSyncDTO): MutableMap<String, LocalDateTime?> {
        val cursorTimestampMap = mutableMapOf<String, LocalDateTime?>()

        syncDTO.workouts.populateCursorInfos(cursorTimestampMap, Workout::class)
        syncDTO.workoutGroups.populateCursorInfos(cursorTimestampMap, WorkoutGroup::class)
        syncDTO.exercises.populateCursorInfos(cursorTimestampMap, Exercise::class)
        syncDTO.videos.populateCursorInfos(cursorTimestampMap, Video::class)
        syncDTO.videoExercises.populateCursorInfos(cursorTimestampMap, VideoExercise::class)
        syncDTO.exerciseExecutions.populateCursorInfos(cursorTimestampMap, ExerciseExecution::class)
        syncDTO.videoExerciseExecutions.populateCursorInfos(cursorTimestampMap, VideoExerciseExecution::class)
        syncDTO.workoutGroupsPreDefinitions.populateCursorInfos(cursorTimestampMap, WorkoutGroupPreDefinition::class)
        syncDTO.exercisePredefinitions.populateCursorInfos(cursorTimestampMap, ExercisePreDefinition::class)
        syncDTO.videoExercisePreDefinitions.populateCursorInfos(cursorTimestampMap, VideoExercisePreDefinition::class)
        syncDTO.reports.populateCursorInfos(cursorTimestampMap, Report::class)
        syncDTO.workoutReports.populateCursorInfos(cursorTimestampMap, WorkoutReport::class)
        syncDTO.metadata.populateCursorInfos(cursorTimestampMap, HealthConnectMetadata::class)
        syncDTO.steps.populateCursorInfos(cursorTimestampMap, HealthConnectSteps::class)
        syncDTO.caloriesBurned.populateCursorInfos(cursorTimestampMap, HealthConnectCaloriesBurned::class)
        syncDTO.heartRateSessions.populateCursorInfos(cursorTimestampMap, HealthConnectHeartRate::class)
        syncDTO.heartRateSamples.populateCursorInfos(cursorTimestampMap, HealthConnectHeartRateSamples::class)
        syncDTO.sleepSessions.populateCursorInfos(cursorTimestampMap, HealthConnectSleepSession::class)
        syncDTO.sleepStages.populateCursorInfos(cursorTimestampMap, HealthConnectSleepStages::class)
        syncDTO.sleepSessionAssociations.populateCursorInfos(cursorTimestampMap, SleepSessionExerciseExecution::class)

        return cursorTimestampMap
    }
}