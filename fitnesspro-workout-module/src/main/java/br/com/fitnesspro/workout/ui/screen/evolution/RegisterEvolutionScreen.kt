package br.com.fitnesspro.workout.ui.screen.evolution

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.fab.FitnessProFloatingActionButton
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonSave
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonCamera
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonGallery
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.menu.DefaultExposedDropdownMenu
import br.com.fitnesspro.compose.components.gallery.video.callbacks.OnVideoClick
import br.com.fitnesspro.compose.components.gallery.video.components.VideoGallery
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.extensions.formatSimpleTime
import br.com.fitnesspro.core.extensions.launchVideosOnly
import br.com.fitnesspro.core.extensions.openCameraVideo
import br.com.fitnesspro.core.theme.ChronometerStyle
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.evolution.callbacks.OnInactivateExecutionClick
import br.com.fitnesspro.workout.ui.screen.evolution.callbacks.OnSaveExerciseExecution
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnFinishVideoRecording
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnOpenCameraVideo
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnVideoSelectedOnGallery
import br.com.fitnesspro.workout.ui.screen.exercise.enums.EnumExerciseScreenTags
import br.com.fitnesspro.workout.ui.state.RegisterEvolutionUIState
import br.com.fitnesspro.workout.ui.viewmodel.RegisterEvolutionViewModel
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import br.com.fitnesspro.core.R as CoreRes

@Composable
fun RegisterEvolutionScreen(
    viewModel: RegisterEvolutionViewModel,
    onBackClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    RegisterEvolutionScreen(
        state = state,
        onBackClick = onBackClick,
        onOpenCameraVideo = viewModel::onOpenCameraVideo,
        onFinishVideoRecording = viewModel::onFinishVideoRecording,
        onVideoSelectedOnGallery = viewModel::onVideoSelectedOnGallery,
        onVideoClick = viewModel::onVideoClick,
        onSaveRegisterEvolution = viewModel::onSaveRegisterEvolution,
        onExecuteLoad = viewModel::loadUIStateWithDatabaseInfos,
        onVideoDeleteClick = viewModel::onDeleteVideo,
        onInactivateExecutionClick = viewModel::onInactivateExecution,
        onStopChronometer = viewModel::onStopChronometer,
        onUpdateChronometer = viewModel::onUpdateChronometer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterEvolutionScreen(
    state: RegisterEvolutionUIState,
    onBackClick: () -> Unit = { },
    onOpenCameraVideo: OnOpenCameraVideo? = null,
    onFinishVideoRecording: OnFinishVideoRecording? = null,
    onVideoSelectedOnGallery: OnVideoSelectedOnGallery? = null,
    onVideoClick: OnVideoClick? = null,
    onSaveRegisterEvolution: OnSaveExerciseExecution? = null,
    onExecuteLoad: () -> Unit = { },
    onVideoDeleteClick: (String, () -> Unit) -> Unit = { _, _ -> },
    onInactivateExecutionClick: OnInactivateExecutionClick? = null,
    onStopChronometer: () -> Unit = {},
    onUpdateChronometer: suspend () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                state.onFabVisibilityChange(available.y >= 0)
                return Offset.Zero
            }
        }
    }

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

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = state.bottomBarVisible,
                enter = expandVertically(
                    expandFrom = Alignment.Bottom,
                    animationSpec = tween(durationMillis = 500)
                ) + fadeIn(),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Bottom,
                    animationSpec = tween(durationMillis = 500)
                ) + fadeOut()
            ) {
                FitnessProBottomAppBar(
                    modifier = Modifier.imePadding(),
                    actions = {
                        IconButtonDelete(
                            onClick = {
                                onInactivateExecutionClick?.onExecute {
                                    state.onToggleLoading()
                                    onBackClick()
                                }
                            }
                        )
                    },
                    floatingActionButton = {
                        if (state.toExerciseExecution.executionEndTime != null) {
                            FloatingActionButtonSave(
                                iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                onClick = {
                                    onSave(keyboardController, state, onSaveRegisterEvolution, coroutineScope, snackbarHostState, context)
                                }
                            )
                        } else {
                            FitnessProFloatingActionButton(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                onClick = { onStopChronometer() },
                                content = {
                                    Icon(
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        painter = painterResource(CoreRes.drawable.ic_timer_pause_24dp),
                                        contentDescription = stringResource(R.string.label_end_execution)
                                    )
                                }
                            )
                        }
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(modifier = Modifier.padding(8.dp)) {
                    Text(text = it.visuals.message, style = SnackBarTextStyle)
                }
            }
        },
        contentWindowInsets = WindowInsets.ime
    ) {

        LaunchedEffect(state.executeLoad) {
            if (state.executeLoad) {
                onExecuteLoad()
            }
        }

        LaunchedEffect(state.chronometerRunning) {
            if (state.chronometerRunning) {
                onUpdateChronometer()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it)
        ) {
            FitnessProLinearProgressIndicator(show = state.showLoading)
            FitnessProMessageDialog(state.messageDialogState)

            ConstraintLayout(
                Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                    .nestedScroll(nestedScrollConnection)
                    .verticalScroll(scrollState)
            ) {
                val (weightRef, repsRef, restRef, restUnitRef, durationRef, durationUnitRef,
                    videoGalleryRef, chronometerRef) = createRefs()

                if (state.chronometerRunning) {
                    Box(
                        Modifier
                            .constrainAs(chronometerRef) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                top.linkTo(parent.top)
                            }
                            .background(MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.small)
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = state.chronometerTime.formatSimpleTime(),
                            style = ChronometerStyle,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .constrainAs(weightRef) {
                            val topAnchor = if (state.chronometerRunning) chronometerRef.bottom else parent.top

                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(topAnchor, margin = 8.dp)

                            width = Dimension.fillToConstraints
                        },
                    field = state.weight,
                    label = stringResource(R.string.register_evolution_weight_label),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .constrainAs(repsRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(weightRef.bottom, margin = 8.dp)

                            width = Dimension.fillToConstraints
                        },
                    field = state.repetitions,
                    label = stringResource(R.string.register_evolution_reps_label),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .constrainAs(restRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(repsRef.bottom, margin = 8.dp)

                            width = Dimension.fillToConstraints
                        },
                    field = state.rest,
                    label = stringResource(R.string.register_evolution_rest_label),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )

                DefaultExposedDropdownMenu(
                    modifier = Modifier.constrainAs(restUnitRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(restRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                    },
                    field = state.restUnit,
                    labelResId = R.string.register_evolution_rest_unit_label,
                    showClearOption = true,
                    clearOptionText = stringResource(R.string.exercise_screen_label_clear_unit)
                )

                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .constrainAs(durationRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(restUnitRef.bottom, margin = 8.dp)

                            width = Dimension.fillToConstraints
                        },
                    field = state.duration,
                    label = stringResource(R.string.register_evolution_duration_label),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )

                DefaultExposedDropdownMenu(
                    modifier = Modifier.constrainAs(durationUnitRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(durationRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                    },
                    field = state.durationUnit,
                    labelResId = R.string.register_evolution_duration_unit_label,
                    showClearOption = true,
                    clearOptionText = stringResource(R.string.exercise_screen_label_clear_unit)
                )

                VideoGallery(
                    modifier = Modifier.constrainAs(videoGalleryRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(durationUnitRef.bottom, margin = 8.dp)
                        bottom.linkTo(parent.bottom)
                    },
                    state = state.videoGalleryState,
                    onVideoClick = onVideoClick,
                    onVideoDeleteClick = { filePath ->
                        onVideoDeleteClick(filePath) {
                            showMessageDeleteVideoSuccess(coroutineScope, context, snackbarHostState)
                        }
                    },
                    emptyMessage = stringResource(R.string.register_evolution_screen_videos_empty_message),
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
    }
}

private fun showMessageDeleteVideoSuccess(
    coroutineScope: CoroutineScope,
    context: Context,
    snackbarHostState: SnackbarHostState
) {
    coroutineScope.launch {
        val message = context.getString(R.string.video_delete_success_message)
        snackbarHostState.showSnackbar(message = message)
    }
}

private fun onSave(
    keyboardController: SoftwareKeyboardController?,
    state: RegisterEvolutionUIState,
    onSaveClick: OnSaveExerciseExecution?,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    keyboardController?.hide()
    state.onToggleLoading()
    Firebase.analytics.logButtonClick(EnumExerciseScreenTags.EXERCISE_SCREEN_KEYBOARD_SAVE)

    onSaveClick?.onExecute {
        state.onToggleLoading()
        showSuccessMessage(coroutineScope, snackbarHostState, context)
    }
}

private fun showSuccessMessage(coroutineScope: CoroutineScope, snackbarHostState: SnackbarHostState, context: Context) {
    coroutineScope.launch {
        val message = context.getString(R.string.register_evolution_screen_success_message)
        snackbarHostState.showSnackbar(message = message)
    }
}

@Preview
@Composable
private fun RegisterEvolutionScreenLightPreview() {
    FitnessProTheme {
        Surface {
            RegisterEvolutionScreen(
                state = defaultRegisterEvolutionState
            )
        }
    }
}


@Preview
@Composable
private fun RegisterEvolutionScreenDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            RegisterEvolutionScreen(
                state = defaultRegisterEvolutionState
            )
        }
    }
}