package br.com.fitnesspro.workout.ui.screen.current.workout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.list.LazyVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.state.CurrentWorkoutUIState
import br.com.fitnesspro.workout.ui.viewmodel.CurrentWorkoutViewModel

@Composable
fun CurrentWorkoutScreen(
    viewModel: CurrentWorkoutViewModel,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    CurrentWorkoutScreen(
        state = state,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWorkoutScreen(
    state: CurrentWorkoutUIState,
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = stringResource(R.string.current_workout_title),
                subtitle = state.subtitle,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        ConstraintLayout(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            val (listRef) = createRefs()

            FitnessProMessageDialog(state = state.messageDialogState)

            LazyVerticalList(
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(listRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                items = state.items,
                emptyMessageResId = R.string.current_workout_empty_message
            ) {
                CurrentWorkoutItem(it)
            }
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CurrentWorkoutScreenPreview() {
    FitnessProTheme {
        Surface {
            CurrentWorkoutScreen(
                state = currentWorkoutState
            )
        }
    }
}


@Preview(device = "id:small_phone")
@Composable
private fun CurrentWorkoutScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            CurrentWorkoutScreen(
                state = currentWorkoutState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CurrentWorkoutScreenEmptyPreview() {
    FitnessProTheme {
        Surface {
            CurrentWorkoutScreen(
                state = currentWorkoutEmptyState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CurrentWorkoutScreenEmptyPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            CurrentWorkoutScreen(
                state = currentWorkoutEmptyState
            )
        }
    }
}