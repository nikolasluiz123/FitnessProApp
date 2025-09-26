package br.com.fitnesspro.workout.ui.screen.evolution

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import br.com.fitnesspro.common.ui.navigation.GeneratedReportsScreenArgs
import br.com.fitnesspro.common.ui.screen.report.callback.OnNavigateToReports
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonNewReport
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonViewReports
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.filter.SimpleFilter
import br.com.fitnesspro.compose.components.list.PagedLazyVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.navigation.ExecutionChartScreenArgs
import br.com.fitnesspro.workout.ui.screen.evolution.callbacks.OnGenerateWorkoutReportClick
import br.com.fitnesspro.workout.ui.screen.evolution.callbacks.OnNavigateToExecutionGroupedBarChart
import br.com.fitnesspro.workout.ui.state.ExecutionEvolutionHistoryUIState
import br.com.fitnesspro.workout.ui.viewmodel.ExecutionEvolutionHistoryViewModel

@Composable
fun ExecutionEvolutionHistoryScreen(
    viewModel: ExecutionEvolutionHistoryViewModel,
    onBackClick: () -> Unit,
    onHistoryClick: OnNavigateToExecutionGroupedBarChart,
    onNavigateToReports: OnNavigateToReports
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ExecutionEvolutionHistoryScreen(
        state = state,
        onBackClick = onBackClick,
        onExecuteLoad = viewModel::onExecuteLoad,
        onHistoryClick = onHistoryClick,
        onNavigateToReports = onNavigateToReports,
        onGenerateReportClick = viewModel::onGenerateReport,
        onShowReportDialog = viewModel::onShowReportDialog,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExecutionEvolutionHistoryScreen(
    state: ExecutionEvolutionHistoryUIState = ExecutionEvolutionHistoryUIState(),
    onBackClick: () -> Unit = {},
    onExecuteLoad: () -> Unit = {},
    onHistoryClick: OnNavigateToExecutionGroupedBarChart? = null,
    onNavigateToReports: OnNavigateToReports? = null,
    onGenerateReportClick: OnGenerateWorkoutReportClick? = null,
    onShowReportDialog: () -> Unit = { },
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = stringResource(R.string.execution_evolution_history_screen_title),
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            FitnessProBottomAppBar(
                actions = {
                    IconButtonViewReports(
                        iconModifier = Modifier.size(32.dp),
                        onClick = {
                            onNavigateToReports?.onExecute(
                                args = GeneratedReportsScreenArgs(
                                    subtitle = context.getString(R.string.workout_reports_subtitle),
                                    reportContext = EnumReportContext.WORKOUT_REGISTER_EVOLUTION
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
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .consumeWindowInsets(it)
                .fillMaxSize()
        ) {
            LaunchedEffect(state.executeLoad) {
                if (state.executeLoad) {
                    onExecuteLoad()
                }
            }

            FitnessProMessageDialog(state.messageDialogState)

            NewRegisterEvolutionReportDialog(
                state = state.newRegisterEvolutionReportDialogUIState,
                onGenerateClick = onGenerateReportClick
            )

            SimpleFilter(
                modifier = Modifier.fillMaxWidth(),
                state = state.simpleFilterState,
                placeholderResId = R.string.execution_evolution_history_screen_simple_filter_placeholder
            ) {
                HistoryList(
                    state = state,
                    onClick = onHistoryClick
                )
            }

            HistoryList(
                state = state,
                onClick = onHistoryClick
            )
        }
    }
}

@Composable
private fun HistoryList(
    state: ExecutionEvolutionHistoryUIState,
    onClick: OnNavigateToExecutionGroupedBarChart?
) {
    PagedLazyVerticalList(
        modifier = Modifier.fillMaxSize(),
        pagingItems = state.history.collectAsLazyPagingItems(),
        emptyMessageResId = R.string.members_evolution_empty_message,
    ) { tuple ->
        ExecutionEvolutionListItem(
            tuple = tuple,
            onClick = {
                onClick?.onNavigate(
                    ExecutionChartScreenArgs(
                        exerciseId = tuple.exerciseId!!
                    )
                )
            }
        )
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExecutionEvolutionHistoryScreenLightPreview() {
    FitnessProTheme {
        Surface {
            ExecutionEvolutionHistoryScreen(
                state = defaultExecutionEvolutionHistoryState
            )
        }
    }
}


@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExecutionEvolutionHistoryScreenDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExecutionEvolutionHistoryScreen(
                state = defaultExecutionEvolutionHistoryState
            )
        }
    }
}