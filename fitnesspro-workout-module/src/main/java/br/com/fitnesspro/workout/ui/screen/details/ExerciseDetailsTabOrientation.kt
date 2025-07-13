package br.com.fitnesspro.workout.ui.screen.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.compose.components.gallery.video.callbacks.OnVideoClick
import br.com.fitnesspro.compose.components.gallery.video.components.VideoGallery
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.DayWeekWorkoutItem
import br.com.fitnesspro.workout.ui.state.ExerciseDetailsUIState

@Composable
fun ExerciseDetailsTabOrientations(
    state: ExerciseDetailsUIState,
    onVideoClick: OnVideoClick? = null
) {
    ConstraintLayout(Modifier.fillMaxSize()) {
        val (itemLayoutRef, videoGalleryRef) = createRefs()

        DayWeekWorkoutItem(
            toExercise = state.toExercise,
            enabled = false,
            showDivider = false,
            showExerciseName = false,
            modifier = Modifier.constrainAs(itemLayoutRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        VideoGallery(
            modifier = Modifier
                .padding(top = 12.dp, start = 8.dp, end = 8.dp, bottom = 48.dp)
                .constrainAs(videoGalleryRef) {
                    top.linkTo(itemLayoutRef.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            state = state.videoGalleryState,
            onVideoClick = onVideoClick,
            emptyMessage = stringResource(R.string.exercise_screen_videos_empty_message),
        )
    }
}

@Preview
@Composable
private fun ExerciseDetailsTabOrientationsPreview() {
    FitnessProTheme {
        Surface {
            ExerciseDetailsTabOrientations(
                state = defaultExerciseDetailsUIState
            )
        }
    }
}

@Preview
@Composable
private fun ExerciseDetailsTabOrientationsPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseDetailsTabOrientations(
                state = defaultExerciseDetailsUIState
            )
        }
    }
}