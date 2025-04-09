package br.com.fitnesspro.workout.ui.screen.workout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.LabeledText
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.list.LazyVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.workout.decorator.CurrentWorkoutItemDecorator
import br.com.fitnesspro.workout.ui.state.CurrentWorkoutUIState
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWorkoutScreen(
    state: CurrentWorkoutUIState
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title!!,
                subtitle = state.subtitle!!
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

@Composable
fun CurrentWorkoutItem(decorator: CurrentWorkoutItemDecorator) {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val (dayWeekRef, muscularGroupRef, dividerRef) = createRefs()

        createHorizontalChain(dayWeekRef, muscularGroupRef)

        LabeledText(
            modifier = Modifier.constrainAs(dayWeekRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top, margin = 8.dp)

                width = Dimension.fillToConstraints
            },
            label = stringResource(R.string.current_workout_item_label_day_week),
            value = decorator.dayWeek.getFirstPartFullDisplayName(),
        )

        LabeledText(
            modifier = Modifier.constrainAs(muscularGroupRef) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)

                width = Dimension.fillToConstraints
            },
            label = stringResource(R.string.current_workout_item_label_workout_groups),
            value = decorator.muscularGroups,
            textAlign = TextAlign.End
        )

        HorizontalDivider(
            modifier = Modifier.constrainAs(dividerRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                top.linkTo(dayWeekRef.bottom, margin = 8.dp)
            }
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CurrentWorkoutItemPreview() {
    FitnessProTheme {
        Surface {
            CurrentWorkoutItem(
                decorator = CurrentWorkoutItemDecorator(
                    dayWeek = DayOfWeek.MONDAY,
                    muscularGroups = "Peito, Costas, Ombros"
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CurrentWorkoutScreenPreview() {
    FitnessProTheme {
        Surface {
            CurrentWorkoutScreen(
                state = CurrentWorkoutUIState(
                    title = "Treino Atual",
                    subtitle = "01/05/2024 até 01/07/2024",
                    items = listOf(
                        CurrentWorkoutItemDecorator(
                            dayWeek = DayOfWeek.MONDAY,
                            muscularGroups = "Peito, Ombro e Tríceps"
                        ),
                        CurrentWorkoutItemDecorator(
                            dayWeek = DayOfWeek.WEDNESDAY,
                            muscularGroups = "Costas, Ombro e Bíceps"
                        ),
                        CurrentWorkoutItemDecorator(
                            dayWeek = DayOfWeek.FRIDAY,
                            muscularGroups = "Perna Completa"
                        )
                    )
                )
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
                state = CurrentWorkoutUIState(
                    title = "Treino Atual",
                    subtitle = "01/05/2024 até 01/07/2024"
                )
            )
        }
    }
}