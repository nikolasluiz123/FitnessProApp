package br.com.fitnesspro.scheduler.ui.screen.scheduler

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonSave
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.scheduler.callback.OnSaveSchedulerConfigClick
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_FAB_SAVE
import br.com.fitnesspro.scheduler.ui.state.SchedulerConfigUIState
import br.com.fitnesspro.scheduler.ui.viewmodel.SchedulerConfigViewModel
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SchedulerConfigScreen(
    viewModel: SchedulerConfigViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    SchedulerConfigScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onSaveClick = viewModel::save
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerConfigScreen(
    state: SchedulerConfigUIState,
    onNavigateBack: () -> Unit = { },
    onSaveClick: OnSaveSchedulerConfigClick? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = stringResource(R.string.scheduler_config_screen_title),
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButtonSave(
                modifier = Modifier.testTag(SCHEDULER_CONFIG_SCREEN_FAB_SAVE.name),
                onClick = {
                    state.onToggleLoading()
                    Firebase.analytics.logButtonClick(SCHEDULER_CONFIG_SCREEN_FAB_SAVE)
                    onSaveClick?.onExecute {
                        keyboardController?.hide()
                        state.onToggleLoading()
                        showSuccessMessage(
                            coroutineScope = coroutineScope,
                            snackbarHostState = snackbarHostState,
                            context = context
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(modifier = Modifier.padding(8.dp)) {
                    Text(text = it.visuals.message, style = SnackBarTextStyle)
                }
            }
        },
        contentWindowInsets = WindowInsets.ime
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            FitnessProLinearProgressIndicator(show = state.showLoading)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                FitnessProMessageDialog(state = state.messageDialogState)

                when (state.toPerson?.user?.type) {
                    EnumUserType.PERSONAL_TRAINER, EnumUserType.NUTRITIONIST -> {
                        ProfessionalSchedulerConfigScreen(
                            state = state,
                            onDone = {
                                state.onToggleLoading()
                                onSaveClick?.onExecute {
                                    state.onToggleLoading()
                                    showSuccessMessage(
                                        coroutineScope = coroutineScope,
                                        snackbarHostState = snackbarHostState,
                                        context = context
                                    )
                                }
                            }
                        )
                    }

                    EnumUserType.ACADEMY_MEMBER -> {
                        MemberSchedulerConfigScreen(state)
                    }

                    else -> { }
                }
            }
        }
    }
}

private fun showSuccessMessage(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(message = context.getString(R.string.scheduler_config_screen_message_success))
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerConfigScreenMemberPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SchedulerConfigScreen(
                state = schedulerConfigMemberState,
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerConfigScreenProfessionalPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SchedulerConfigScreen(
                state = schedulerConfigPersonalState,
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerConfigScreenProfessionalPopulatedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SchedulerConfigScreen(
                state = schedulerConfigPersonalPopulatedState,
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerConfigScreenMemberPreviewLight() {
    FitnessProTheme {
        Surface {
            SchedulerConfigScreen(
                state = schedulerConfigMemberState,
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerConfigScreenProfessionalPreviewLight() {
    FitnessProTheme {
        Surface {
            SchedulerConfigScreen(
                state = schedulerConfigPersonalState,
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerConfigScreenProfessionalPopulatedPreviewLight() {
    FitnessProTheme {
        Surface {
            SchedulerConfigScreen(
                state = schedulerConfigPersonalPopulatedState,
            )
        }
    }
}