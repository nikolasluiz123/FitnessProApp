package br.com.fitnesspro.workout.ui.screen.exercise

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonCamera
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonGallery
import br.com.fitnesspro.compose.components.gallery.video.components.VideoGallery
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.core.extensions.launchVideosOnly
import br.com.fitnesspro.core.extensions.openCameraVideo
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnFinishVideoRecording
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnOpenCameraVideo
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnVideoSelectedOnGallery
import br.com.fitnesspro.workout.ui.state.ExerciseUIState

@Composable
fun ExerciseScreenTabVideos(
    state: ExerciseUIState = ExerciseUIState(),
    onOpenCameraVideo: OnOpenCameraVideo? = null,
    onFinishVideoRecording: OnFinishVideoRecording? = null,
    onVideoSelectedOnGallery: OnVideoSelectedOnGallery? = null,
    onVideoClick: (path: String) -> Unit = {}
) {
    val context = LocalContext.current

    val launcherVideo = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        if (success) {
            state.onToggleLoading()
            onFinishVideoRecording?.onExecute(onSuccess = { state.onToggleLoading() })
        }
    }

    val launcherVideosGallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            state.onToggleLoading()

            onVideoSelectedOnGallery?.onExecute(
                uri = it,
                onSuccess = { state.onToggleLoading() }
            )
        }
    }

    Column(Modifier.fillMaxSize()) {
        FitnessProLinearProgressIndicator(show = state.showLoading)

        VideoGallery(
            modifier = Modifier.padding(top = 12.dp),
            state = state.videoGalleryState,
            onVideoClick = onVideoClick,
            emptyMessage = stringResource(R.string.exercise_screen_videos_empty_message),
            actions = {
                IconButtonGallery(
                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = {
                        launcherVideosGallery.launchVideosOnly()
                    }
                )

                IconButtonCamera(
                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = {
                        context.openCameraVideo(launcherVideo) { _, file ->
                            onOpenCameraVideo?.onExecute(file)
                        }
                    }
                )
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