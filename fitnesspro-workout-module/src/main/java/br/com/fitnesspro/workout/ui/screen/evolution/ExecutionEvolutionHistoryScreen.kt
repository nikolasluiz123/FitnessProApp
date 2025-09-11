package br.com.fitnesspro.workout.ui.screen.evolution

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.filter.SimpleFilter
import br.com.fitnesspro.compose.components.list.PagedLazyVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.tuple.ExecutionEvolutionHistoryGroupedTuple
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.state.ExecutionEvolutionHistoryUIState
import br.com.fitnesspro.workout.ui.viewmodel.ExecutionEvolutionHistoryViewModel

@Composable
fun ExecutionEvolutionHistoryScreen(
    viewModel: ExecutionEvolutionHistoryViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ExecutionEvolutionHistoryScreen(
        state = state,
        onBackClick = onBackClick,
        onExecuteLoad = viewModel::loadHistory
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExecutionEvolutionHistoryScreen(
    state: ExecutionEvolutionHistoryUIState = ExecutionEvolutionHistoryUIState(),
    onBackClick: () -> Unit = {},
    onExecuteLoad: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = stringResource(R.string.execution_evolution_history_screen_title),
                onBackClick = onBackClick
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

            SimpleFilter(
                modifier = Modifier.fillMaxWidth(),
                state = state.simpleFilterState,
                placeholderResId = R.string.execution_evolution_history_screen_simple_filter_placeholder
            ) {
                HistoryList(
                    state = state,
                )
            }

            HistoryList(
                state = state,
            )
        }
    }
}

@Composable
private fun HistoryList(
    state: ExecutionEvolutionHistoryUIState,
    onClick: (ExecutionEvolutionHistoryGroupedTuple) -> Unit = {}
) {
    PagedLazyVerticalList(
        modifier = Modifier.fillMaxSize(),
        pagingItems = state.history.collectAsLazyPagingItems(),
        emptyMessageResId = R.string.members_evolution_empty_message,
    ) { tuple ->
        ExecutionEvolutionListItem(
            tuple = tuple,
            onClick = onClick
        )
    }
}