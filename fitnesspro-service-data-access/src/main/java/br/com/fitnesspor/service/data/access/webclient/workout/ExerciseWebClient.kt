package br.com.fitnesspor.service.data.access.webclient.workout

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.workout.IExerciseService
import br.com.fitnesspor.service.data.access.service.workout.IVideoService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.mappers.getExerciseDTO
import br.com.fitnesspro.mappers.getExerciseExecutionDTO
import br.com.fitnesspro.mappers.getExercisePreDefinitionDTO
import br.com.fitnesspro.mappers.getNewVideoExerciseExecutionDTO
import br.com.fitnesspro.mappers.getVideoDTO
import br.com.fitnesspro.mappers.getVideoExerciseDTO
import br.com.fitnesspro.mappers.getVideoExerciseExecutionDTO
import br.com.fitnesspro.mappers.getVideoExercisePreDefinitionDTO
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.model.workout.VideoExercise
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.VideoExercisePreDefinition
import br.com.fitnesspro.shared.communication.dtos.workout.ExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.ExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.ExercisePreDefinitionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.NewVideoExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExercisePreDefinitionDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.WorkoutModuleImportFilter
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import com.google.gson.GsonBuilder

class ExerciseWebClient(
    context: Context,
    private val exerciseService: IExerciseService,
    private val videoService: IVideoService,
): FitnessProWebClient(context) {

    suspend fun saveExerciseBatch(
        token: String,
        exerciseList: List<Exercise>,
        workoutGroupList: List<WorkoutGroup>
    ): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                val groupMap = workoutGroupList.associateBy { it.id }

                val exerciseListDTO = exerciseList.map { exercise ->
                    exercise.getExerciseDTO(groupMap[exercise.workoutGroupId]!!)
                }

                exerciseService.saveExerciseBatch(
                    token = formatToken(token),
                    exerciseDTOList = exerciseListDTO
                ).getResponseBody()
            }
        )
    }

    suspend fun importExercises(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<ExerciseDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                exerciseService.importExercise(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(ExerciseDTO::class.java)
            }
        )
    }

    suspend fun importExerciseVideos(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<VideoExerciseDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                exerciseService.importExerciseVideos(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(VideoExerciseDTO::class.java)
            }
        )
    }

    suspend fun saveExerciseVideoBatch(
        token: String,
        exerciseVideoList: List<VideoExercise>
    ): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                val exerciseVideoListDTO = exerciseVideoList.map(VideoExercise::getVideoExerciseDTO)

                exerciseService.saveExerciseVideoBatch(
                    token = formatToken(token),
                    exerciseVideoDTOList = exerciseVideoListDTO
                ).getResponseBody()
            }
        )
    }

    suspend fun importVideos(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<VideoDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                videoService.importVideos(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(VideoDTO::class.java)
            }
        )
    }

    suspend fun saveVideoBatch(
        token: String,
        videoList: List<Video>
    ): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                val videoListDTO = videoList.map(Video::getVideoDTO)

                videoService.saveVideoBatch(
                    token = formatToken(token),
                    videoDTOList = videoListDTO
                ).getResponseBody()
            }
        )
    }

    suspend fun saveExerciseExecutionBatch(
        token: String,
        exerciseList: List<ExerciseExecution>,
    ): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                val exerciseListDTO = exerciseList.map(ExerciseExecution::getExerciseExecutionDTO)

                exerciseService.saveExerciseExecutionBatch(
                    token = formatToken(token),
                    exerciseDTOs = exerciseListDTO
                ).getResponseBody()
            }
        )
    }

    suspend fun importExerciseExecution(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<ExerciseExecutionDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                exerciseService.importExerciseExecution(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(ExerciseExecutionDTO::class.java)
            }
        )
    }

    suspend fun importVideoExerciseExecution(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<VideoExerciseExecutionDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                exerciseService.importVideoExerciseExecution(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(VideoExerciseExecutionDTO::class.java)
            }
        )
    }

    suspend fun saveExerciseExecutionVideosBatch(
        token: String,
        videosList: List<VideoExerciseExecution>
    ): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                val videosListDTO = videosList.map(VideoExerciseExecution::getVideoExerciseExecutionDTO)

                exerciseService.saveExerciseExecutionVideosBatch(
                    token = formatToken(token),
                    videoDTOs = videosListDTO
                ).getResponseBody()
            }
        )
    }

    suspend fun createExecutionVideo(
        token: String,
        videoExecution: VideoExerciseExecution,
        video: Video
    ): PersistenceServiceResponse<NewVideoExerciseExecutionDTO> {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                videoService.createExecutionVideo(
                    token = formatToken(token),
                    newVideoExecutionDTO = videoExecution.getNewVideoExerciseExecutionDTO(video)
                ).getResponseBody(NewVideoExerciseExecutionDTO::class.java)
            }
        )
    }

    suspend fun saveExercisePreDefinitionBatch(
        token: String,
        exerciseList: List<ExercisePreDefinition>,
    ): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                val exerciseListDTO = exerciseList.map(ExercisePreDefinition::getExercisePreDefinitionDTO)

                exerciseService.saveExercisePreDefinitionBatch(
                    token = formatToken(token),
                    exerciseDTOs = exerciseListDTO
                ).getResponseBody()
            }
        )
    }

    suspend fun importExercisePreDefinition(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<ExercisePreDefinitionDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                exerciseService.importExercisePreDefinition(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(ExercisePreDefinitionDTO::class.java)
            }
        )
    }

    suspend fun importVideoExercisePreDefinition(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<VideoExercisePreDefinitionDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                exerciseService.importVideoExercisePreDefinition(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(VideoExercisePreDefinitionDTO::class.java)
            }
        )
    }

    suspend fun saveExercisePreDefinitionVideosBatch(
        token: String,
        videosList: List<VideoExercisePreDefinition>
    ): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                val videosListDTO = videosList.map(VideoExercisePreDefinition::getVideoExercisePreDefinitionDTO)

                exerciseService.saveExercisePreDefinitionVideosBatch(
                    token = formatToken(token),
                    videoDTOs = videosListDTO
                ).getResponseBody()
            }
        )
    }
}