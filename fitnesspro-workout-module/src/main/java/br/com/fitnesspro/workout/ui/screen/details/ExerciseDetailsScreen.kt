package br.com.fitnesspro.workout.ui.screen.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.android.ui.compose.components.buttons.fab.BaseFloatingActionButton
import br.com.android.ui.compose.components.tabs.BaseHorizontalPager
import br.com.android.ui.compose.components.tabs.BaseTabRow
import br.com.android.ui.compose.components.topbar.SimpleTopAppBar
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.navigation.RegisterEvolutionScreenArgs
import br.com.fitnesspro.workout.ui.screen.details.enums.EnumTabsExerciseDetailsScreen
import br.com.fitnesspro.workout.ui.screen.evolution.callbacks.OnNavigateToRegisterEvolution
import br.com.fitnesspro.workout.ui.state.ExerciseDetailsUIState
import br.com.fitnesspro.workout.ui.viewmodel.ExerciseDetailsViewModel
import br.com.fitnesspro.core.R as CoreRes

@Composable
fun ExerciseDetailsScreen(
    viewModel: ExerciseDetailsViewModel,
    onBackClick: () -> Unit,
    onNavigateToRegisterEvolution: OnNavigateToRegisterEvolution
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ExerciseDetailsScreen(
        state = state,
        onBackClick = onBackClick,
        onNavigateToRegisterEvolution = onNavigateToRegisterEvolution,
        onExecuteLoad = viewModel::loadUIStateWithDatabaseInfos
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailsScreen(
    state: ExerciseDetailsUIState,
    onBackClick: () -> Unit = {},
    onNavigateToRegisterEvolution: OnNavigateToRegisterEvolution? = null,
    onExecuteLoad: () -> Unit = { }
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = stringResource(R.string.exercise_details_screen_title),
                subtitle = state.subtitle,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (state.tabState.selectedTab.enum == EnumTabsExerciseDetailsScreen.EVOLUTION) {
                BaseFloatingActionButton(
                    onClick = {
                        state.toExercise.id?.let { exerciseId ->
                            onNavigateToRegisterEvolution?.onNavigate(
                                args = RegisterEvolutionScreenArgs(exerciseId = exerciseId)
                            )
                        }
                    },
                    content = {
                        Icon(
                            painter = painterResource(CoreRes.drawable.ic_timer_play_24dp),
                            contentDescription = stringResource(R.string.label_start_execution)
                        )
                    }
                )
            }
        }
    ) { paddings ->
        Column(
            Modifier
                .padding(paddings)
                .consumeWindowInsets(paddings)
                .fillMaxSize()
        ) {
            FitnessProMessageDialog(state.messageDialogState)

            LaunchedEffect(state.executeLoad) {
                if (state.executeLoad) {
                    onExecuteLoad()
                }
            }

            ConstraintLayout(
                Modifier
                    .fillMaxSize()
            ) {
                val (tabRowRef, horizontalPagerRef) = createRefs()

                val pagerState = rememberPagerState(pageCount = state.tabState::tabsSize)

                BaseTabRow(
                    modifier = Modifier.constrainAs(tabRowRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                    tabState = state.tabState,
                    coroutineScope = coroutineScope,
                    pagerState = pagerState
                )

                BaseHorizontalPager(
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

@Preview(apiLevel = 35, device = "id:small_phone")
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


@Preview(apiLevel = 35, device = "id:small_phone")
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