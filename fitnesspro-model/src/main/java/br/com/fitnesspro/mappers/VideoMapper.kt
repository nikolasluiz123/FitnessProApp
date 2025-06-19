package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.model.workout.VideoExercise
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