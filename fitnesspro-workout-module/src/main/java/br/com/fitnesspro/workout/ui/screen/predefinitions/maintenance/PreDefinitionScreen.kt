package br.com.fitnesspro.workout.ui.screen.predefinitions.maintenance

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonSave
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonCamera
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonGallery
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.PagedListDialogOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.menu.DefaultExposedDropdownMenu
import br.com.fitnesspro.compose.components.gallery.video.callbacks.OnVideoClick
import br.com.fitnesspro.compose.components.gallery.video.components.VideoGallery
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.extensions.launchVideosOnly
import br.com.fitnesspro.core.extensions.openCameraVideo
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.exercise.ExercisePagedDialogItem
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnFinishVideoRecording
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnOpenCameraVideo
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnVideoSelectedOnGallery
import br.com.fitnesspro.workout.ui.screen.predefinitions.maintenance.callbacks.OnInactivatePreDefinitionClick
import br.com.fitnesspro.workout.ui.screen.predefinitions.maintenance.callbacks.OnSavePreDefinitionClick
import br.com.fitnesspro.workout.ui.screen.predefinitions.maintenance.enums.EnumPreDefinitionScreenTags
import br.com.fitnesspro.workout.ui.state.PreDefinitionUIState
import br.com.fitnesspro.workout.ui.viewmodel.PreDefinitionViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PreDefinitionScreen(
    viewModel: PreDefinitionViewModel,
    onBackClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    PreDefinitionScreen(
        state = state,
        onBackClick = onBackClick,
        onOpenCameraVideo = viewModel::onOpenCameraVideo,
        onFinishVideoRecording = viewModel::onFinishVideoRecording,
        onVideoSelectedOnGallery = viewModel::onVideoSelectedOnGallery,
        onVideoClick = viewModel::onVideoClick,
        onSaveClick = viewModel::onSavePreDefinition
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreDefinitionScreen(
    state: PreDefinitionUIState,
    onBackClick: () -> Unit = { },
    onSaveClick: OnSavePreDefinitionClick? = null,
    onInactivateClick: OnInactivatePreDefinitionClick? = null,
    onOpenCameraVideo: OnOpenCameraVideo? = null,
    onFinishVideoRecording: OnFinishVideoRecording? = null,
    onVideoSelectedOnGallery: OnVideoSelectedOnGallery? = null,
    onVideoClick: OnVideoClick? = null,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

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
            FitnessProBottomAppBar(
                actions = {
                    IconButtonDelete(
                        onClick = {
                            onInactivateClick?.onExecute {
                                state.onToggleLoading()
                                onBackClick()
                            }
                        },
                        enabled = state.inactivateEnabled
                    )
                },
                floatingActionButton = {
                    FloatingActionButtonSave(
                        iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        onClick = {
                            onSave(keyboardController, state, onSaveClick, coroutineScope, snackbarHostState, context)
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
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FitnessProLinearProgressIndicator(show = state.showLoading)
            FitnessProMessageDialog(state.messageDialogState)

            ConstraintLayout(
                Modifier
                    .consumeWindowInsets(paddingValues)
                    .padding(horizontal = 8.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                val (groupRef, exerciseRef, setsRef, repsRef, restRef, unitRestRef,
                    durationRef, unitDurationRef, exerciseOrderRef, videoGalleryRef) = createRefs()

                if (state.showGroupField) {
                    OutlinedTextFieldValidation(
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(groupRef) {
                                top.linkTo(parent.top, margin = 8.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        field = state.group,
                        label = stringResource(R.string.pre_definition_screen_label_group),
                    )
                }

                PagedListDialogOutlinedTextFieldValidation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(exerciseRef) {
                            val topAnchor = if (state.showGroupField) groupRef.bottom else parent.top

                            top.linkTo(topAnchor, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    field = state.exercise,
                    fieldLabel = stringResource(R.string.pre_definition_screen_label_exercise),
                    simpleFilterPlaceholderResId = R.string.pre_definition_screen_exercises_place_holder,
                    emptyMessage = R.string.pre_definition_screen_exercises_empty_message,
                    itemLayout = {
                        ExercisePagedDialogItem(
                            toExercise = it,
                            onItemClick = state.exercise.dialogListState.onDataListItemClick
                        )
                    }
                )

                if (state.showGroupField) {
                    OutlinedTextFieldValidation(
                        modifier = Modifier
                            .constrainAs(exerciseOrderRef) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                top.linkTo(exerciseRef.bottom, margin = 8.dp)

                                width = Dimension.fillToConstraints
                            },
                        field = state.exerciseOrder,
                        label = stringResource(R.string.pre_definition_screen_label_exercise_order),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    )
                }

                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .constrainAs(setsRef) {
                            val topAnchor = if (state.showGroupField) exerciseOrderRef.bottom else exerciseRef.bottom

                            top.linkTo(topAnchor, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)

                            width = Dimension.fillToConstraints
                        },
                    field = state.sets,
                    label = stringResource(R.string.pre_definition_screen_label_sets),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedTextFieldValidation(
                    modifier = Modifier.constrainAs(repsRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(setsRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                    },
                    field = state.reps,
                    label = stringResource(R.string.pre_definition_screen_label_reps),
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
                    label = stringResource(R.string.pre_definition_screen_label_rest),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )

                )

                DefaultExposedDropdownMenu(
                    modifier = Modifier.constrainAs(unitRestRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(restRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                    },
                    field = state.unitRest,
                    labelResId = R.string.pre_definition_screen_label_unit,
                    showClearOption = true,
                    clearOptionText = stringResource(R.string.pre_definition_screen_label_clear_unit)
                )

                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .constrainAs(durationRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(unitRestRef.bottom, margin = 8.dp)

                            width = Dimension.fillToConstraints
                        },
                    field = state.duration,
                    label = stringResource(R.string.pre_definition_screen_label_duration),
                )

                DefaultExposedDropdownMenu(
                    modifier = Modifier.constrainAs(unitDurationRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(durationRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                    },
                    field = state.unitDuration,
                    labelResId = R.string.pre_definition_screen_label_unit,
                    showClearOption = true,
                    clearOptionText = stringResource(R.string.pre_definition_screen_label_clear_unit)
                )

                VideoGallery(
                    modifier = Modifier.constrainAs(videoGalleryRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(unitDurationRef.bottom, margin = 8.dp)
                        bottom.linkTo(parent.bottom, margin = 12.dp)
                    },
                    state = state.videoGalleryState,
                    onVideoClick = onVideoClick,
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

private fun onSave(
    keyboardController: SoftwareKeyboardController?,
    state: PreDefinitionUIState,
    onSaveClick: OnSavePreDefinitionClick?,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    keyboardController?.hide()
    state.onToggleLoading()
    Firebase.analytics.logButtonClick(EnumPreDefinitionScreenTags.PRE_DEFINITION_SCREEN_KEYBOARD_SAVE)

    onSaveClick?.onExecute {
        state.onToggleLoading()
        showSuccessMessage(coroutineScope, snackbarHostState, context)
    }
}

private fun showSuccessMessage(coroutineScope: CoroutineScope, snackbarHostState: SnackbarHostState, context: Context) {
    coroutineScope.launch {
        val message = context.getString(R.string.pre_definition_screen_success_message)
        snackbarHostState.showSnackbar(message = message)
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun PreDefinitionScreenPreview() {
    FitnessProTheme {
        Surface {
            PreDefinitionScreen(
                state = preDefinitionDefaultState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun PreDefinitionScreenGroupedPreview() {
    FitnessProTheme {
        Surface {
            PreDefinitionScreen(
                state = preDefinitionGroupedState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun PreDefinitionScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            PreDefinitionScreen(
                state = preDefinitionDefaultState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun PreDefinitionScreenGroupedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            PreDefinitionScreen(
                state = preDefinitionGroupedState
            )
        }
    }
}