package br.com.fitnesspro.workout.ui.screen.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.LabeledText
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.list.grouped.LazyGroupedVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.extensions.toDurationFormatted
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_700
import br.com.fitnesspro.core.theme.LabelGroupTextStyle
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.workout.decorator.DayWeekWorkoutGroupDecorator
import br.com.fitnesspro.workout.ui.screen.workout.decorator.DayWeekWorkoutItemDecorator
import br.com.fitnesspro.workout.ui.state.DayWeekWorkoutUIState
import java.time.LocalTime

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
                    DayWeekWorkoutGroupItem(groupDecorator)
                },
                itemLayout = { itemDecorator ->
                    DayWeekWorkoutItem(itemDecorator)
                }
            )
        }
    }
}

@Composable
fun DayWeekWorkoutGroupItem(groupDecorator: DayWeekWorkoutGroupDecorator) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(color = GREY_700)
            .padding(8.dp)
    ) {
        Text(
            modifier = Modifier.align(alignment = Alignment.Center),
            text = groupDecorator.label,
            style = LabelGroupTextStyle,
            color = Color.White
        )
    }
}

@Composable
fun DayWeekWorkoutItem(decorator: DayWeekWorkoutItemDecorator) {
    val context = LocalContext.current

    ConstraintLayout(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        val (exerciseRef, durationRef, setsAndRepsRef, restRef, observationRef, dividerRef) = createRefs()

        LabeledText(
            modifier = Modifier.constrainAs(exerciseRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            label = stringResource(R.string.day_week_workout_screen_exercise),
            value = decorator.exercise
        )

        if (decorator.duration != null) {
            createHorizontalChain(durationRef, restRef)

            LabeledText(
                modifier = Modifier.constrainAs(durationRef) {
                    top.linkTo(exerciseRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)

                    width = Dimension.fillToConstraints
                },
                label = stringResource(R.string.day_week_workout_screen_duration),
                value = decorator.duration.toDurationFormatted(context)!!
            )
        } else {
            createHorizontalChain(setsAndRepsRef, restRef)

            LabeledText(
                modifier = Modifier.constrainAs(setsAndRepsRef) {
                    top.linkTo(exerciseRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)

                    width = Dimension.fillToConstraints
                },
                label = stringResource(R.string.day_week_workout_screen_sets_and_repetitions),
                value = stringResource(
                    R.string.day_week_workout_screen_sets_and_repetitions_value,
                    decorator.sets!!,
                    decorator.repetitions!!
                )
            )
        }

        LabeledText(
            modifier = Modifier.constrainAs(restRef) {
                top.linkTo(exerciseRef.bottom, margin = 8.dp)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            label = stringResource(R.string.day_week_workout_screen_rest),
            value = decorator.rest?.toDurationFormatted(context) ?: stringResource(R.string.day_week_workout_screen_no_rest),
            textAlign = TextAlign.End
        )

        if (!decorator.observation.isNullOrEmpty()) {
            LabeledText(
                modifier = Modifier.constrainAs(observationRef) {
                    if (decorator.duration != null) {
                        top.linkTo(durationRef.bottom, margin = 8.dp)
                    } else {
                        top.linkTo(setsAndRepsRef.bottom, margin = 8.dp)
                    }

                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
                label = stringResource(R.string.day_week_workout_screen_observation),
                value = decorator.observation,
                maxLinesValue = 5
            )
        }

        HorizontalDivider(
            modifier = Modifier.constrainAs(dividerRef) {
                if (!decorator.observation.isNullOrEmpty()) {
                    top.linkTo(observationRef.bottom, margin = 8.dp)
                } else {
                    top.linkTo(restRef.bottom, margin = 8.dp)
                }

                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun GroupItemPreview() {
    FitnessProTheme {
        Surface {
            DayWeekWorkoutGroupItem(
                DayWeekWorkoutGroupDecorator(
                    id = "1",
                    label = "Aquecimento",
                    items = emptyList()
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekWorkoutItemPreview() {
    FitnessProTheme {
        Surface {
            DayWeekWorkoutItem(
                DayWeekWorkoutItemDecorator(
                    id = "1",
                    exercise = "Esteira",
                    duration = LocalTime.of(0, 30).toSecondOfDay().toLong(),
                    sets = null,
                    repetitions = null,
                    rest = null,
                    observation = "Se vier para a academia caminhando não é necessário fazer esse aquecimento"
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekWorkoutItem2Preview() {
    FitnessProTheme {
        Surface {
            DayWeekWorkoutItem(
                DayWeekWorkoutItemDecorator(
                    id = "1",
                    exercise = "Manguito Rotador na Polia",
                    duration = null,
                    sets = 4,
                    repetitions = 12,
                    rest = 45,
                    observation = "Alternar entre os ombros e utilizar cargas reduzidas"
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekWorkoutItem3Preview() {
    FitnessProTheme {
        Surface {
            DayWeekWorkoutItem(
                DayWeekWorkoutItemDecorator(
                    id = "1",
                    exercise = "Supino Inclinado com Halteres",
                    duration = null,
                    sets = 4,
                    repetitions = 12,
                    rest = 45,
                    observation = null
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun DayWeekWorkoutScreenPreview() {
    FitnessProTheme {
        Surface {
            DayWeekWorkoutScreen(
                state = DayWeekWorkoutUIState(
                    title = "Treino de Segunda",
                    subtitle = "Peito, Ombro e Tríceps",
                    dayWeekWorkoutGroups = listOf(
                        DayWeekWorkoutGroupDecorator(
                            id = "1",
                            label = "Aquecimento",
                            items = listOf(
                                DayWeekWorkoutItemDecorator(
                                    id = "1",
                                    exercise = "Esteira",
                                    duration = LocalTime.of(0, 30).toSecondOfDay().toLong(),
                                    sets = null,
                                    repetitions = null,
                                    rest = null,
                                    observation = "Se vier para a academia caminhando não é necessário fazer esse aquecimento"
                                ),
                                DayWeekWorkoutItemDecorator(
                                    id = "2",
                                    exercise = "Manguito Rotador na Polia",
                                    duration = null,
                                    sets = 4,
                                    repetitions = 12,
                                    rest = null,
                                    observation = "Alternar entre os ombros e utilizar cargas reduzidas"
                                )
                            )
                        ),
                        DayWeekWorkoutGroupDecorator(
                            id = "2",
                            label = "Peito",
                            items = listOf(
                                DayWeekWorkoutItemDecorator(
                                    id = "1",
                                    exercise = "Supino Inclinado com Halteres",
                                    duration = null,
                                    sets = 4,
                                    repetitions = 12,
                                    rest = 45,
                                    observation = "Segurar embaixo por 2 segundos em toda repetição"
                                ),
                                DayWeekWorkoutItemDecorator(
                                    id = "2",
                                    exercise = "Crussifixo Reto com Halteres",
                                    duration = null,
                                    sets = 4,
                                    repetitions = 12,
                                    rest = 45,
                                    observation = null
                                )
                            )
                        )
                    ),
                )
            )
        }
    }
}