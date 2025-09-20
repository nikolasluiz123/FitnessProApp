package br.com.fitnesspro.workout.repository.sync.importation

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.common.ImportSegregationResult
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.mappers.getExercise
import br.com.fitnesspro.mappers.getExerciseExecution
import br.com.fitnesspro.mappers.getExercisePreDefinition
import br.com.fitnesspro.mappers.getVideo
import br.com.fitnesspro.mappers.getVideoExercise
import br.com.fitnesspro.mappers.getVideoExerciseExecution
import br.com.fitnesspro.mappers.getVideoExercisePreDefinition
import br.com.fitnesspro.mappers.getWorkout
import br.com.fitnesspro.mappers.getWorkoutGroup
import br.com.fitnesspro.mappers.getWorkoutGroupPreDefinition
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.model.workout.VideoExercise
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.VideoExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
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
    override suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): WorkoutModuleImportationFilter {
        val person = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!)
        return WorkoutModuleImportationFilter(lastUpdateDate = lastUpdateDate, personId = person.id)
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
            else -> throw IllegalArgumentException("Não foi possível recuperar o DAO. Classe de modelo inválida.")
        }
    }
}