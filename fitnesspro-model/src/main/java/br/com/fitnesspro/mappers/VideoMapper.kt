package br.com.fitnesspro.mappers

import br.com.android.room.toolkit.model.enums.EnumDownloadState
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.core.android.utils.media.FileUtils
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.model.workout.VideoExercise
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution
import br.com.fitnesspro.model.workout.predefinition.VideoExercisePreDefinition
import br.com.fitnesspro.shared.communication.dtos.workout.VideoDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExercisePreDefinitionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IVideoDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IVideoExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IVideoExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IVideoExercisePreDefinitionDTO
import br.com.fitnesspro.to.TOVideo
import br.com.fitnesspro.to.TOVideoExercise
import br.com.fitnesspro.to.TOVideoExerciseExecution
import br.com.fitnesspro.to.TOVideoExercisePreDefinition

fun TOVideo.getVideo(): Video {
    val model = Video(
        extension = extension,
        filePath = filePath,
        date = date,
        kbSize = kbSize,
        seconds = seconds,
        width = width,
        height = height,
        active = active,
        storageDownloadState = getVideoDownloadState(filePath)
    )

    this.id?.let { model.id = it }

    return model
}

fun IVideoDTO.getVideo(): Video {
    return Video(
        id = id!!,
        extension = extension,
        filePath = filePath,
        date = date!!,
        kbSize = kbSize,
        seconds = seconds,
        width = width,
        height = height,
        active = active,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        storageTransmissionState = if (storageTransmissionDate != null) EnumTransmissionState.TRANSMITTED else EnumTransmissionState.PENDING,
        storageUrl = storageUrl,
        storageTransmissionDate = storageTransmissionDate,
        storageDownloadState = getVideoDownloadState(filePath)
    )
}

fun TOVideoExercise.getVideoExercise(): VideoExercise {
    val model = VideoExercise(
        exerciseId = exerciseId,
        videoId = toVideo?.id,
        active = active
    )

    this.id?.let { model.id = it }

    return model
}

fun IVideoExerciseDTO.getVideoExercise(): VideoExercise {
    return VideoExercise(
        id = id!!,
        exerciseId = exerciseId,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        videoId = videoId,
        active = active
    )
}

fun VideoExercise.getVideoExerciseDTO(): VideoExerciseDTO {
    return VideoExerciseDTO(
        id = id,
        exerciseId = exerciseId,
        videoId = videoId,
        active = active
    )
}

fun Video.getVideoDTO(): VideoDTO {
    return VideoDTO(
        id = id,
        extension = extension,
        filePath = filePath,
        date = date,
        kbSize = kbSize,
        seconds = seconds,
        width = width,
        height = height,
        active = active,
        storageUrl = storageUrl,
        storageTransmissionDate = storageTransmissionDate
    )
}

fun TOVideoExerciseExecution.getVideoExerciseExecution(): VideoExerciseExecution {
    val model = VideoExerciseExecution(
        exerciseExecutionId = exerciseExecutionId,
        videoId = toVideo?.id,
        active = active
    )

    this.id?.let { model.id = it }

    return model
}

fun VideoExerciseExecution.getVideoExerciseExecutionDTO(): VideoExerciseExecutionDTO {
    return VideoExerciseExecutionDTO(
        id = id,
        exerciseExecutionId = exerciseExecutionId,
        videoId = videoId,
        active = active
    )
}

fun TOVideoExercisePreDefinition.getVideoExercisePreDefinition(): VideoExercisePreDefinition {
    val model = VideoExercisePreDefinition(
        exercisePreDefinitionId = exercisePreDefinitionId,
        videoId = toVideo?.id,
        active = active
    )

    id?.let { model.id = it }

    return model
}

fun VideoExercisePreDefinition.getVideoExercisePreDefinitionDTO(): VideoExercisePreDefinitionDTO {
    return VideoExercisePreDefinitionDTO(
        id = id,
        exercisePreDefinitionId = exercisePreDefinitionId,
        videoId = videoId,
        active = active
    )
}

fun IVideoExerciseExecutionDTO.getVideoExerciseExecution(): VideoExerciseExecution {
    return VideoExerciseExecution(
        id = id!!,
        exerciseExecutionId = exerciseExecutionId,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        videoId = videoId,
        active = active
    )
}

fun IVideoExercisePreDefinitionDTO.getVideoExercisePreDefinition(): VideoExercisePreDefinition {
    return VideoExercisePreDefinition(
        id = id!!,
        exercisePreDefinitionId = exercisePreDefinitionId,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        videoId = videoId,
        active = active
    )
}

private fun getVideoDownloadState(filePath: String?): EnumDownloadState {
    val exists = filePath?.let { FileUtils.getFileExists(it) } ?: false
    return if (exists) EnumDownloadState.DOWNLOADED else EnumDownloadState.PENDING
}
