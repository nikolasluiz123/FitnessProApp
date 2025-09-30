package br.com.fitnesspro.workout.ui.screen.exercise

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.android.firebase.toolkit.analytics.logButtonClick
import br.com.android.ui.compose.components.bottombar.BaseBottomAppBar
import br.com.android.ui.compose.components.styles.SnackBarTextStyle
import br.com.android.ui.compose.components.tabs.BaseHorizontalPager
import br.com.android.ui.compose.components.tabs.BaseTabRow
import br.com.android.ui.compose.components.topbar.SimpleTopAppBar
import br.com.android.ui.compose.components.video.callbacks.OnVideoClick
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonSave
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnFinishVideoRecording
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnInactivateExerciseClick
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnSaveExerciseClick
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnVideoSelectedOnGallery
import br.com.fitnesspro.workout.ui.screen.exercise.enums.EnumExerciseScreenTags
import br.com.fitnesspro.workout.ui.screen.exercise.enums.EnumTabsExerciseScreen
import br.com.fitnesspro.workout.ui.state.ExerciseUIState
import br.com.fitnesspro.workout.ui.viewmodel.ExerciseViewModel
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ExerciseScreen(
    viewModel: ExerciseViewModel,
    onBackClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ExerciseScreen(
        state = state,
        onBackClick = onBackClick,
        onSaveExerciseClick = viewModel::saveExercise,
        onOpenCameraVideo = viewModel::onOpenCameraVideo,
        onFinishVideoRecording = viewModel::onFinishVideoRecording,
        onVideoSelectedOnGallery = viewModel::onVideoSelectedOnGallery,
        onVideoClick = viewModel::onVideoClick,
        onVideoDeleteClick = viewModel::onDeleteVideo,
        onInactivateExerciseClick = viewModel::onInactivateExercise,
        onExecuteLoad = viewModel::loadUIStateWithDatabaseInfos
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    state: ExerciseUIState = ExerciseUIState(),
    onBackClick: () -> Unit = {},
    onSaveExerciseClick: OnSaveExerciseClick? = null,
    onOpenCameraVideo: (File) -> Unit = { },
    onFinishVideoRecording: OnFinishVideoRecording? = null,
    onVideoSelectedOnGallery: OnVideoSelectedOnGallery? = null,
    onVideoClick: OnVideoClick? = null,
    onVideoDeleteClick: (String, () -> Unit) -> Unit = { _, _ -> },
    onInactivateExerciseClick: OnInactivateExerciseClick? = null,
    onExecuteLoad: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            BaseBottomAppBar(
                modifier = Modifier.imePadding(),
                actions = {
                    IconButtonDelete(
                        onClick = {
                            onInactivateExerciseClick?.onExecute {
                                state.onToggleLoading()
                                onBackClick()
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButtonSave(
                        iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        onClick = {
                            onSave(keyboardController, state, onSaveExerciseClick, coroutineScope, snackbarHostState, context)
                        }
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(modifier = Modifier.padding(8.dp)) {
                    Text(text = it.visuals.message, style = SnackBarTextStyle)
                }
            }
        }
    ) { paddings ->
        LaunchedEffect(state.executeLoad) {
            if (state.executeLoad) {
                onExecuteLoad()
            }
        }

        Column(
            Modifier
                .padding(paddings)
                .consumeWindowInsets(paddings)
                .fillMaxSize()
        ) {
            FitnessProMessageDialog(state.messageDialogState)

            ConstraintLayout(
                Modifier
                    .fillMaxSize()
            ) {
                val (tabRowRef, horizontalPagerRef) = createRefs()

                val pagerState = rememberPagerState(pageCount = state.tabState::tabsSize)

                BaseTabRow(
                    modifier = Modifier.constrainAs(tabRowRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                    tabState = state.tabState,
                    coroutineScope = coroutineScope,
                    pagerState = pagerState
                )

                BaseHorizontalPager(
                    modifier = Modifier.constrainAs(horizontalPagerRef) {
                        start.linkTo(parent.start)
                        top.linkTo(tabRowRef.bottom)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)

                        height = Dimension.fillToConstraints
                    },
                    pagerState = pagerState,
                    tabState = state.tabState
                ) { index ->
                    val enum = EnumTabsExerciseScreen.entries.first { it.index == index }

                    when (enum) {
                        EnumTabsExerciseScreen.GENERAL -> {
                            ExerciseScreenTabGeneral(
                                state = state,
                                onDone = {
                                    onSave(keyboardController, state, onSaveExerciseClick, coroutineScope, snackbarHostState, context)
                                }
                            )
                        }

                        EnumTabsExerciseScreen.VIDEOS -> {
                            ExerciseScreenTabVideos(
                                state = state,
                                onOpenCameraVideo = onOpenCameraVideo,
                                onFinishVideoRecording = onFinishVideoRecording,
                                onVideoSelectedOnGallery = onVideoSelectedOnGallery,
                                onVideoClick = onVideoClick,
                                onVideoDeleteClick = onVideoDeleteClick,
                                coroutineScope = coroutineScope,
                                context = context,
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun onSave(
    keyboardController: SoftwareKeyboardController?,
    state: ExerciseUIState,
    onSaveExerciseClick: OnSaveExerciseClick?,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    keyboardController?.hide()
    state.onToggleLoading()
    Firebase.analytics.logButtonClick(EnumExerciseScreenTags.EXERCISE_SCREEN_KEYBOARD_SAVE)

    onSaveExerciseClick?.onExecute {
        state.onToggleLoading()
        showSuccessMessage(coroutineScope, snackbarHostState, context)
    }
}

private fun showSuccessMessage(coroutineScope: CoroutineScope, snackbarHostState: SnackbarHostState, context: Context) {
    coroutineScope.launch {
        val message = context.getString(R.string.exercise_screen_success_message)
        snackbarHostState.showSnackbar(message = message)
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExerciseScreenNewPreview() {
    FitnessProTheme {
        Surface {
            ExerciseScreen(
                state = exerciseNewUIState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExerciseScreenNewPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseScreen(
                state = exerciseNewUIState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExerciseScreenPreview() {
    FitnessProTheme {
        Surface {
            ExerciseScreen(
                state = exerciseTabGeneralUIState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExerciseScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseScreen(
                state = exerciseTabGeneralUIState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExerciseScreenTabVideosPreview() {
    FitnessProTheme {
        Surface {
            ExerciseScreen(
                state = exerciseTabVideosUIState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExerciseScreenTabVideosPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseScreen(
                state = exerciseTabVideosUIState
            )
        }
    }
}