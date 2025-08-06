package br.com.fitnesspro.scheduler.ui.screen.scheduler

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fitnesspro.common.ui.navigation.GeneratedReportsScreenArgs
import br.com.fitnesspro.common.ui.screen.report.callback.OnNavigateToReports
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.fab.FitnessProFloatingActionButton
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonConfig
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonMessage
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonNewReport
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonViewReports
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.details.callbacks.OnNavigateToCompromise
import br.com.fitnesspro.scheduler.ui.screen.scheduler.callback.OnDayClick
import br.com.fitnesspro.scheduler.ui.screen.scheduler.callback.OnGenerateSchedulerReportClick
import br.com.fitnesspro.scheduler.ui.screen.scheduler.components.DaysGrid
import br.com.fitnesspro.scheduler.ui.screen.scheduler.components.SchedulerHeader
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTags.SCHEDULER_SCREEN_BUTTON_CONFIG
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTags.SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB
import br.com.fitnesspro.scheduler.ui.state.SchedulerUIState
import br.com.fitnesspro.scheduler.ui.viewmodel.SchedulerViewModel
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

@Composable
fun SchedulerScreen(
    viewModel: SchedulerViewModel,
    onBackClick: () -> Unit,
    onDayClick: OnDayClick,
    onNavigateToCompromise: OnNavigateToCompromise,
    onNavigateToConfig: () -> Unit,
    onNavigateToChatHistory: () -> Unit,
    onNavigateToReports: OnNavigateToReports
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SchedulerScreen(
        state = state,
        onBackClick = onBackClick,
        onDayClick = onDayClick,
        onNavigateToCompromise = onNavigateToCompromise,
        onNavigateToConfig = onNavigateToConfig,
        onUpdateSchedules = viewModel::updateSchedules,
        onNavigateToChatHistory = onNavigateToChatHistory,
        onNavigateToReports = onNavigateToReports,
        onGenerateReportClick = viewModel::onGenerateReport,
        onShowReportDialog = viewModel::onShowReportDialog,
        onExecuteLoad = viewModel::loadUIStateWithDatabaseInfos
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerScreen(
    state: SchedulerUIState,
    onBackClick: () -> Unit = { },
    onDayClick: OnDayClick? = null,
    onNavigateToCompromise: OnNavigateToCompromise? = null,
    onNavigateToConfig: () -> Unit = { },
    onUpdateSchedules: () -> Unit = { },
    onNavigateToChatHistory: () -> Unit  = {},
    onNavigateToReports: OnNavigateToReports? = null,
    onGenerateReportClick: OnGenerateSchedulerReportClick? = null,
    onShowReportDialog: () -> Unit = { },
    onExecuteLoad: () -> Unit = { }
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                onBackClick = onBackClick,
                actions = {
                    IconButtonMessage(
                        iconModifier = Modifier.size(24.dp),
                        onClick = onNavigateToChatHistory,
                    )

                    IconButtonConfig(
                        modifier = Modifier.testTag(SCHEDULER_SCREEN_BUTTON_CONFIG.name),
                        onClick = {
                            Firebase.analytics.logButtonClick(SCHEDULER_SCREEN_BUTTON_CONFIG)
                            onNavigateToConfig()
                        }
                    )
                }
            )
        },
        bottomBar = {
            FitnessProBottomAppBar(
                floatingActionButton = {
                    if (state.isVisibleFabRecurrentScheduler) {
                        FitnessProFloatingActionButton(
                            modifier = Modifier.testTag(SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB.name),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            content = {
                                Icon(
                                    painter = painterResource(br.com.fitnesspro.core.R.drawable.ic_edit_calendar_24dp),
                                    contentDescription = stringResource(R.string.schedule_screen_fab_recurrent_description),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            },
                            onClick = {
                                Firebase.analytics.logButtonClick(SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB)

                                onNavigateToCompromise?.onExecute(
                                    args = br.com.fitnesspro.scheduler.ui.navigation.CompromiseScreenArgs(
                                        recurrent = true
                                    )
                                )
                            }
                        )
                    }
                },
                actions = {
                    IconButtonViewReports(
                        iconModifier = Modifier.size(32.dp),
                        onClick = {
                            onNavigateToReports?.onExecute(
                                args = GeneratedReportsScreenArgs(
                                    subtitle = context.getString(R.string.scheduler_reports_subtitle),
                                    reportContext = EnumReportContext.SCHEDULERS_REPORT
                                )
                            )
                        }
                    )

                    IconButtonNewReport(
                        iconModifier = Modifier.size(32.dp),
                        onClick = onShowReportDialog
                    )
                }
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->

        LaunchedEffect(state.executeLoad) {
            if (state.executeLoad) {
                onExecuteLoad()
            }
        }

        LaunchedEffect(Unit) {
            onUpdateSchedules()
        }

        ConstraintLayout(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val (headerRef, daysGridRef) = createRefs()

            FitnessProMessageDialog(state = state.messageDialogState)

            NewSchedulerReportDialog(
                state = state.newSchedulerReportDialogUIState,
                onGenerateClick = onGenerateReportClick
            )

            SchedulerHeader(
                selectedYearMonth = state.selectedYearMonth,
                onBackClick = state.onSelectYearMonth,
                onForwardClick = state.onSelectYearMonth,
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(headerRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)

                        width = Dimension.fillToConstraints
                    }
            )

            DaysGrid(
                modifier = Modifier
                    .constrainAs(daysGridRef) {
                    top.linkTo(headerRef.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                state = state,
                onDayClick = onDayClick
            )
        }

    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
fun ScheduleScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SchedulerScreen(
                state = defaultSchedulerScreenState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
fun ScheduleScreenPersonalPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SchedulerScreen(
                state = personalSchedulerScreenState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
fun ScheduleScreenPreviewLight() {
    FitnessProTheme {
        Surface {
            SchedulerScreen(
                state = defaultSchedulerScreenState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
fun ScheduleScreenPersonalPreviewLight() {
    FitnessProTheme {
        Surface {
            SchedulerScreen(
                state = personalSchedulerScreenState
            )
        }
    }
}