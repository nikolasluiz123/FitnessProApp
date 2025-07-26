package br.com.fitnesspro.workout.ui.screen.predefinitions.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonAdd
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.filter.SimpleFilter
import br.com.fitnesspro.compose.components.list.PagedLazyVerticalList
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.navigation.PreDefinitionScreenArgs
import br.com.fitnesspro.workout.ui.screen.predefinitions.maintenance.callbacks.OnNavigateToPreDefinition
import br.com.fitnesspro.workout.ui.screen.predefinitions.search.bottomsheet.BottomSheetNewPreDefinition
import br.com.fitnesspro.workout.ui.state.PreDefinitionsUIState
import br.com.fitnesspro.workout.ui.viewmodel.PreDefinitionsViewModel

@Composable
fun PreDefinitionsScreen(
    viewModel: PreDefinitionsViewModel,
    onBackClick: () -> Unit,
    onNavigateToPreDefinition: OnNavigateToPreDefinition
) {
    val state by viewModel.uiState.collectAsState()

    PreDefinitionsScreen(
        state = state,
        onBackClick = onBackClick,
        onNavigateToPreDefinition = onNavigateToPreDefinition
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreDefinitionsScreen(
    state: PreDefinitionsUIState,
    onBackClick: () -> Unit = { },
    onNavigateToPreDefinition: OnNavigateToPreDefinition? = null
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = stringResource(R.string.pre_definitions_screen_title),
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButtonAdd(
                onClick = {
                    state.onToggleBottomSheetNewPredefinition()
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
            FitnessProLinearProgressIndicator(state.showLoading)
            FitnessProMessageDialog(state.messageDialogState)

            SimpleFilter(
                modifier = Modifier.fillMaxWidth(),
                state = state.simpleFilterState,
                placeholderResId = R.string.pre_definitions_simple_filter_placeholder
            ) {
                PreDefinitionList(
                    state = state,
                    onNavigateToPreDefinition = onNavigateToPreDefinition
                )
            }

            PreDefinitionList(
                state = state,
                onNavigateToPreDefinition = onNavigateToPreDefinition
            )

            if (state.showBottomSheetNewPredefinition) {
                BottomSheetNewPreDefinition(
                    onDismissRequest = state.onToggleBottomSheetNewPredefinition,
                    onItemClickListener = onNavigateToPreDefinition
                )
            }
        }
    }
}

@Composable
private fun PreDefinitionList(
    state: PreDefinitionsUIState,
    onNavigateToPreDefinition: OnNavigateToPreDefinition?
) {
    PagedLazyVerticalList(
        modifier = Modifier.fillMaxSize(),
        pagingItems = state.predefinitions.collectAsLazyPagingItems(),
        emptyMessageResId = R.string.pre_definitions_empty_message,
    ) { tuple ->
        if (tuple.isGroup) {
            PreDefinitionGroupItem(tuple)
        } else {
            ExercisePreDefinitionItem(
                predefinition = tuple,
                onItemClick = { clickedTuple ->
                    onNavigateToPreDefinition?.onNavigate(
                        args = PreDefinitionScreenArgs(
                            grouped = false,
                            exercisePreDefinitionId = clickedTuple.id
                        )
                    )
                }
            )
        }
    }
}