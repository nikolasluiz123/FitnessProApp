package br.com.fitnesspro.scheduler.ui.screen.scheduler

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.buttons.fab.FitnessProFloatingActionButton
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonConfig
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonMessage
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.details.callbacks.OnNavigateToCompromise
import br.com.fitnesspro.scheduler.ui.screen.scheduler.callback.OnDayClick
import br.com.fitnesspro.scheduler.ui.screen.scheduler.components.DaysGrid
import br.com.fitnesspro.scheduler.ui.screen.scheduler.components.SchedulerHeader
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTags.SCHEDULER_SCREEN_BUTTON_CONFIG
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTags.SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB
import br.com.fitnesspro.scheduler.ui.state.SchedulerUIState
import br.com.fitnesspro.scheduler.ui.viewmodel.SchedulerViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@Composable
fun SchedulerScreen(
    viewModel: SchedulerViewModel,
    onBackClick: () -> Unit,
    onDayClick: OnDayClick,
    onNavigateToCompromise: OnNavigateToCompromise,
    onNavigateToConfig: () -> Unit,
    onNavigateToChatHistory: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    SchedulerScreen(
        state = state,
        onBackClick = onBackClick,
        onDayClick = onDayClick,
        onNavigateToCompromise = onNavigateToCompromise,
        onNavigateToConfig = onNavigateToConfig,
        onUpdateSchedules = viewModel::updateSchedules,
        onNavigateToChatHistory = onNavigateToChatHistory
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
    onNavigateToChatHistory: () -> Unit  = {}
) {
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
        floatingActionButton = {
            if (state.isVisibleFabRecurrentScheduler) {
                FitnessProFloatingActionButton(
                    modifier = Modifier.testTag(SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB.name),
                    content = {
                        Icon(
                            painter = painterResource(br.com.fitnesspro.core.R.drawable.ic_edit_calendar_24dp),
                            contentDescription = stringResource(R.string.schedule_screen_fab_recurrent_description)
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
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->

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

@Preview(device = "id:small_phone")
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

@Preview(device = "id:small_phone")
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