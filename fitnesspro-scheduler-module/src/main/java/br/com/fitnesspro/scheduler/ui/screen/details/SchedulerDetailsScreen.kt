package br.com.fitnesspro.scheduler.ui.screen.details

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonAdd
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.list.LazyVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.parseToLocalDate
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.navigation.CompromiseScreenArgs
import br.com.fitnesspro.scheduler.ui.screen.details.callbacks.OnNavigateToCompromise
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_FAB_ADD
import br.com.fitnesspro.scheduler.ui.state.SchedulerDetailsUIState
import br.com.fitnesspro.scheduler.ui.viewmodel.SchedulerDetailsViewModel
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import java.time.ZoneId

@Composable
fun SchedulerDetailsScreen(
    viewModel: SchedulerDetailsViewModel,
    onBackClick: () -> Unit,
    onNavigateToCompromise: OnNavigateToCompromise
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SchedulerDetailsScreen(
        state = state,
        onBackClick = onBackClick,
        onUpdateSchedules = viewModel::updateSchedules,
        onNavigateToCompromise = onNavigateToCompromise,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerDetailsScreen(
    state: SchedulerDetailsUIState,
    onBackClick: () -> Unit = { },
    onUpdateSchedules: () -> Unit = { },
    onNavigateToCompromise: OnNavigateToCompromise? = null,
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (state.isVisibleFabAdd) {
                FloatingActionButtonAdd(
                    modifier = Modifier.testTag(SCHEDULER_DETAILS_SCREEN_FAB_ADD.name),
                    onClick = {
                        Firebase.analytics.logButtonClick(SCHEDULER_DETAILS_SCREEN_FAB_ADD)
                        onNavigateToCompromise?.onExecute(
                            args = CompromiseScreenArgs(
                                date = state.subtitle.parseToLocalDate(EnumDateTimePatterns.DATE)!!,
                                recurrent = false,
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
                .fillMaxSize()
                .padding(padding)
        ) {
            val listRef = createRef()

            FitnessProMessageDialog(state = state.messageDialogState)

            LazyVerticalList(
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(listRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                items = state.schedules,
                emptyMessageResId = getEmptyMessage(state),
            ) { toScheduler ->
                SchedulerDetailItem(
                    to = toScheduler,
                    state = state,
                    onNavigateToCompromise = onNavigateToCompromise
                )
            }
        }
    }
}

private fun getEmptyMessage(state: SchedulerDetailsUIState): Int {
    val date = state.subtitle.parseToLocalDate(EnumDateTimePatterns.DATE)!!

    return if (date < dateNow(ZoneId.systemDefault())) {
        R.string.scheduler_details_empty_message_past_date
    } else {
        R.string.scheduler_details_empty_message
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerDetailsPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SchedulerDetailsScreen(
                state = populatedListState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerDetailsEmptyPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SchedulerDetailsScreen(
                state = professionalState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerDetailsPreviewLight() {
    FitnessProTheme {
        Surface {
            SchedulerDetailsScreen(
                state = populatedListState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerDetailsEmptyPreviewLight() {
    FitnessProTheme {
        Surface {
            SchedulerDetailsScreen(
                state = professionalState
            )
        }
    }
}