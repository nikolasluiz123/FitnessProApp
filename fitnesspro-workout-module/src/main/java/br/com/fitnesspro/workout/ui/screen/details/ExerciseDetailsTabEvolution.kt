package br.com.fitnesspro.workout.ui.screen.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.compose.collectAsLazyPagingItems
import br.com.android.ui.compose.components.list.PagedLazyVerticalList
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.navigation.RegisterEvolutionScreenArgs
import br.com.fitnesspro.workout.ui.screen.evolution.callbacks.OnNavigateToRegisterEvolution
import br.com.fitnesspro.workout.ui.state.ExerciseDetailsUIState

@Composable
fun ExerciseDetailsTabEvolution(
    state: ExerciseDetailsUIState,
    onNavigateToRegisterEvolution: OnNavigateToRegisterEvolution? = null
) {
    ConstraintLayout(Modifier.fillMaxSize()) {
        val listRef = createRef()

        PagedLazyVerticalList(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(listRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)

                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
            pagingItems = state.evolutionList.collectAsLazyPagingItems(),
            itemLayout = { executionGroupedTuple ->
                if (executionGroupedTuple.isGroup) {
                    ExecutionDateItem(executionGroupedTuple.groupDate!!)
                } else {
                    ExerciseExecutionItem(
                        item = executionGroupedTuple,
                        onItemClick = { executionGroupedTupleClicked ->
                            state.toExercise.id?.let { exerciseId ->
                                onNavigateToRegisterEvolution?.onNavigate(
                                    args = RegisterEvolutionScreenArgs(
                                        exerciseId = exerciseId,
                                        exerciseExecutionId = executionGroupedTupleClicked.id!!
                                    )
                                )
                            }
                        }
                    )
                }
            },
            emptyMessageResId = R.string.exercise_details_tab_evolution_empty_list_message,
        )
    }
}

@Preview
@Composable
private fun ExerciseDetailsTabEvolutionPreview() {
    FitnessProTheme {
        Surface {
            ExerciseDetailsTabEvolution(
                state = defaultExerciseDetailsUIState
            )
        }
    }
}

@Preview
@Composable
private fun ExerciseDetailsTabEvolutionPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseDetailsTabEvolution(
                state = defaultExerciseDetailsUIState
            )
        }
    }
}