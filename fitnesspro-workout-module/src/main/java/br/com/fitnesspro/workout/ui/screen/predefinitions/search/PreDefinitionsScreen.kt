package br.com.fitnesspro.workout.ui.screen.predefinitions.search

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonAdd
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.workout.R
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
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it)
        ) {
            if (state.showBottomSheetNewPredefinition) {
                BottomSheetNewPreDefinition(
                    onDismissRequest = state.onToggleBottomSheetNewPredefinition,
                    onItemClickListener = onNavigateToPreDefinition
                )
            }
        }
    }
}