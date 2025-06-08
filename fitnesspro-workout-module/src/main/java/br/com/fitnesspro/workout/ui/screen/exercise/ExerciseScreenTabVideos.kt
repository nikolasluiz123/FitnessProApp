package br.com.fitnesspro.workout.ui.screen.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.gallery.video.components.VideoGallery
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.state.ExerciseUIState

@Composable
fun ExerciseScreenTabVideos(
    state: ExerciseUIState = ExerciseUIState(),
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 8.dp)) {
        FitnessProLinearProgressIndicator(show = state.showLoading)

        VideoGallery(
            state = state.videoGalleryState,
            onVideoClick = {

            },
            emptyMessage = stringResource(R.string.exercise_screen_videos_empty_message),
            actions = {

            }
        )
    }
}


@Preview(device = "id:small_phone")
@Composable
private fun ExerciseScreenTabVideosNewPreview() {
    FitnessProTheme {
        Surface {
            ExerciseScreenTabVideos(
                state = exerciseNewUIState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ExerciseScreenTabVideosNewPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseScreenTabVideos(
                state = exerciseNewUIState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ExerciseScreenTabVideosPreview() {
    FitnessProTheme {
        Surface {
            ExerciseScreenTabVideos(
                state = exerciseTabGeneralUIState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ExerciseScreenTabVideosPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseScreenTabVideos(
                state = exerciseTabGeneralUIState
            )
        }
    }
}