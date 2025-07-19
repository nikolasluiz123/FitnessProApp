package br.com.fitnesspro.workout.ui.screen.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonAdd
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.tabs.FitnessProHorizontalPager
import br.com.fitnesspro.compose.components.tabs.FitnessProTabRow
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.navigation.RegisterEvolutionScreenArgs
import br.com.fitnesspro.workout.ui.screen.details.enums.EnumTabsExerciseDetailsScreen
import br.com.fitnesspro.workout.ui.screen.evolution.callbacks.OnNavigateToRegisterEvolution
import br.com.fitnesspro.workout.ui.state.ExerciseDetailsUIState
import br.com.fitnesspro.workout.ui.viewmodel.ExerciseDetailsViewModel

@Composable
fun ExerciseDetailsScreen(
    viewModel: ExerciseDetailsViewModel,
    onBackClick: () -> Unit,
    onNavigateToRegisterEvolution: OnNavigateToRegisterEvolution
) {
    val state by viewModel.uiState.collectAsState()

    ExerciseDetailsScreen(
        state = state,
        onBackClick = onBackClick,
        onNavigateToRegisterEvolution = onNavigateToRegisterEvolution
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailsScreen(
    state: ExerciseDetailsUIState,
    onBackClick: () -> Unit = {},
    onNavigateToRegisterEvolution: OnNavigateToRegisterEvolution? = null
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = stringResource(R.string.exercise_details_screen_title),
                subtitle = state.subtitle,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (state.tabState.selectedTab.enum == EnumTabsExerciseDetailsScreen.EVOLUTION)
            FloatingActionButtonAdd(
                onClick = {
                    state.toExercise.id?.let { exerciseId ->
                        onNavigateToRegisterEvolution?.onNavigate(
                            args = RegisterEvolutionScreenArgs(exerciseId = exerciseId)
                        )
                    }
                }
            )
        }
    ) { paddings ->
        Column(
            Modifier
                .padding(paddings)
                .consumeWindowInsets(paddings)
                .fillMaxSize()
        ) {
            FitnessProMessageDialog(state.messageDialogState)

            ConstraintLayout(
                Modifier
                    .fillMaxSize()
            ) {
                val (tabRowRef, horizontalPagerRef) = createRefs()

                val pagerState = rememberPagerState(pageCount = state.tabState::tabsSize)

                FitnessProTabRow(
                    modifier = Modifier.constrainAs(tabRowRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                    tabState = state.tabState,
                    coroutineScope = coroutineScope,
                    pagerState = pagerState
                )

                FitnessProHorizontalPager(
                    modifier = Modifier.constrainAs(horizontalPagerRef) {
                        start.linkTo(parent.start)
                        top.linkTo(tabRowRef.bottom)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)

                        height = Dimension.fillToConstraints
                    },
                    pagerState = pagerState,
                    tabState = state.tabState
                ) { index ->
                    val enum = EnumTabsExerciseDetailsScreen.entries.first { it.index == index }

                    when (enum) {
                        EnumTabsExerciseDetailsScreen.ORIENTATION -> {
                            ExerciseDetailsTabOrientations(state)
                        }

                        EnumTabsExerciseDetailsScreen.EVOLUTION -> {
                            ExerciseDetailsTabEvolution(
                                state = state,
                                onNavigateToRegisterEvolution = onNavigateToRegisterEvolution
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ExerciseDetailsDefaultLightPreview() {
    FitnessProTheme {
        Surface {
            ExerciseDetailsScreen(
                state = defaultExerciseDetailsUIState
            )
        }
    }
}


@Preview
@Composable
private fun ExerciseDetailsDefaultDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseDetailsScreen(
                state = defaultExerciseDetailsUIState
            )
        }
    }
}