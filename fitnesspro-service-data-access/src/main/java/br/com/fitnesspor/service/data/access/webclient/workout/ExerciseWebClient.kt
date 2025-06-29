package br.com.fitnesspor.service.data.access.webclient.workout

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.workout.IExerciseService
import br.com.fitnesspor.service.data.access.service.workout.IVideoService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.mappers.getExerciseDTO
import br.com.fitnesspro.mappers.getVideoDTO
import br.com.fitnesspro.mappers.getVideoExerciseDTO
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.model.workout.VideoExercise
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.shared.communication.dtos.workout.ExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.WorkoutModuleImportFilter
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import com.google.gson.GsonBuilder

class ExerciseWebClient(
    context: Context,
    private val exerciseService: IExerciseService,
    private val videoService: IVideoService,
): FitnessProWebClient(context) {

    suspend fun saveExercise(token: String, exercise: Exercise, workoutGroup: WorkoutGroup) {
        persistenceServiceErrorHandlingBlock(
            codeBlock = {
                exerciseService.saveExercise(
                    token = formatToken(token),
                    exerciseDTO = exercise.getExerciseDTO(workoutGroup)
                ).getResponseBody(ExerciseDTO::class.java)
            }
        )
    }

    suspend fun saveExerciseBatch(
        token: String,
        exerciseList: List<Exercise>,
    ): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                val exerciseListDTO = exerciseList.map { exercise ->
                    exercise.getExerciseDTO(null)
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
}