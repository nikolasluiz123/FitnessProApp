package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.model.workout.VideoExercise
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution
import br.com.fitnesspro.model.workout.predefinition.VideoExercisePreDefinition
import br.com.fitnesspro.shared.communication.dtos.workout.NewVideoExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExercisePreDefinitionDTO
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
        active = active
    )

    this.id?.let { model.id = it }

    return model
}

fun VideoDTO.getVideo(): Video {
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
        storageTransmissionState = EnumTransmissionState.TRANSMITTED
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

fun VideoExerciseDTO.getVideoExercise(): VideoExercise {
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
        active = active
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

fun VideoExerciseExecution.getNewVideoExerciseExecutionDTO(video: Video): NewVideoExerciseExecutionDTO {
    return NewVideoExerciseExecutionDTO(
        id = id,
        exerciseExecutionId = exerciseExecutionId,
        videoDTO = video.getVideoDTO(),
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

fun VideoExerciseExecutionDTO.getVideoExerciseExecution(): VideoExerciseExecution {
    return VideoExerciseExecution(
        id = id!!,
        exerciseExecutionId = exerciseExecutionId,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        videoId = videoId,
        active = active
    )
}

fun VideoExercisePreDefinitionDTO.getVideoExercisePreDefinition(): VideoExercisePreDefinition {
    return VideoExercisePreDefinition(
        id = id!!,
        exercisePreDefinitionId = exercisePreDefinitionId,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        videoId = videoId,
        active = active
    )
}