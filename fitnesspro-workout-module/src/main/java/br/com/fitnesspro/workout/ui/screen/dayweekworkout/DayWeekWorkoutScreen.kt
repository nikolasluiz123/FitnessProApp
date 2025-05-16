package br.com.fitnesspro.workout.ui.screen.dayweekworkout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.list.grouped.LazyGroupedVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.state.DayWeekWorkoutUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayWeekWorkoutScreen(state: DayWeekWorkoutUIState) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                subtitle = state.subtitle
            )
        }
    ) { padding ->
        ConstraintLayout(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val (workoutList) = createRefs()

            FitnessProMessageDialog(state = state.messageDialogState)

            LazyGroupedVerticalList(
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(workoutList) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                groups = state.dayWeekWorkoutGroups,
                emptyMessageResId = R.string.day_week_workout_empty_message,
                groupLayout = { groupDecorator ->
                    WorkoutGroupItem(groupDecorator)
                },
                itemLayout = { itemDecorator ->
                    DayWeekWorkoutItem(itemDecorator)
                }
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun DayWeekWorkoutScreenPreview() {
    FitnessProTheme {
        Surface {
            DayWeekWorkoutScreen(dayWeekWorkoutScreenDefaultState)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun DayWeekWorkoutScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayWeekWorkoutScreen(
                state = dayWeekWorkoutScreenDefaultState
            )
        }
    }
}