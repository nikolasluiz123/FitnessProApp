package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.model.workout.VideoExercise
import br.com.fitnesspro.shared.communication.dtos.workout.NewVideoExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseDTO
import br.com.fitnesspro.to.TOVideo
import br.com.fitnesspro.to.TOVideoExercise

fun TOVideo.getVideo(): Video {
    val model = Video(
        extension = extension,
        filePath = filePath,
        date = date,
        kbSize = kbSize,
        seconds = seconds,
        width = width,
        height = height
    )

    this.id?.let { model.id = it }

    return model
}

fun Video.getTOVideo(): TOVideo {
    return TOVideo(
        id = id,
        extension = extension,
        filePath = filePath,
        date = date
    )
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
        transmissionState = EnumTransmissionState.TRANSMITTED
    )
}

fun TOVideoExercise.getVideoExercise(): VideoExercise {
    val model = VideoExercise(
        exerciseId = exerciseId,
        videoId = toVideo?.id,
    )

    this.id?.let { model.id = it }

    return model
}

fun VideoExercise.getTOVideoExercise(toVideo: TOVideo): TOVideoExercise {
    return TOVideoExercise(
        id = id,
        exerciseId = exerciseId,
        toVideo = toVideo
    )
}

fun VideoExerciseDTO.getVideoExercise(): VideoExercise {
    return VideoExercise(
        id = id!!,
        exerciseId = exerciseId,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        videoId = videoId
    )
}

fun VideoExercise.getVideoExerciseDTO(): VideoExerciseDTO {
    return VideoExerciseDTO(
        id = id,
        exerciseId = exerciseId,
        videoId = videoId,
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
    )
}

fun VideoExercise.getNewVideoExerciseDTO(video: Video): NewVideoExerciseDTO {
    return NewVideoExerciseDTO(
        id = id,
        exerciseId = exerciseId,
        videoDTO = video.getVideoDTO()
    )
}