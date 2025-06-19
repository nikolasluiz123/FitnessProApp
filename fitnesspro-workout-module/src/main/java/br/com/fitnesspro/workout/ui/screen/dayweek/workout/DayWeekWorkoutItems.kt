package br.com.fitnesspro.workout.ui.screen.dayweek.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.LabeledText
import br.com.fitnesspro.core.extensions.toReadableDuration
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.LabelGroupTextStyle
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.decorator.WorkoutGroupDecorator

@Composable
fun WorkoutGroupItem(
    decorator: WorkoutGroupDecorator,
    onItemClick: (WorkoutGroupDecorator) -> Unit = {},
    onItemLongClick: (WorkoutGroupDecorator) -> Unit = {}
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
            .combinedClickable(
                onClick = {
                    onItemClick(decorator)
                },
                onLongClick = {
                    onItemLongClick(decorator)
                }
            )
    ) {
        Text(
            modifier = Modifier.align(alignment = Alignment.Center),
            text = decorator.label,
            style = LabelGroupTextStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DayWeekWorkoutItem(toExercise: TOExercise, onItemClick: (TOExercise) -> Unit = { }) {
    val context = LocalContext.current

    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(toExercise) }
    ) {
        val (exerciseRef, durationRef, setsAndRepsRef, restRef, observationRef, dividerRef) = createRefs()

        LabeledText(
            modifier = Modifier.constrainAs(exerciseRef) {
                top.linkTo(parent.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)

                width = Dimension.fillToConstraints
            },
            label = stringResource(R.string.day_week_workout_screen_exercise),
            value = toExercise.name ?: ""
        )

        if (isShowSetsAndReps(toExercise)) {
            LabeledText(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .constrainAs(setsAndRepsRef) {
                        top.linkTo(exerciseRef.bottom, margin = 8.dp)
                        start.linkTo(parent.start)

                        width = Dimension.fillToConstraints
                    },
                label = getLabelSetsAndReps(toExercise),
                value = getValueSetsAndReps(toExercise)
            )

            LabeledText(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .constrainAs(restRef) {
                        top.linkTo(exerciseRef.bottom, margin = 8.dp)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                    },
                label = stringResource(R.string.day_week_workout_screen_rest),
                value = toExercise.rest?.toReadableDuration(context) ?: stringResource(R.string.day_week_workout_screen_no_rest),
                textAlign = TextAlign.End
            )

            createHorizontalChain(setsAndRepsRef, restRef)
        }

        if (isShowDuration(toExercise)) {
            LabeledText(
                modifier = Modifier
                    .constrainAs(durationRef) {
                        if (isShowSetsAndReps(toExercise)) {
                            top.linkTo(setsAndRepsRef.bottom, margin = 8.dp)
                        } else {
                            top.linkTo(exerciseRef.bottom, margin = 8.dp)
                        }

                        start.linkTo(parent.start, margin = 8.dp)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                    },
                label = stringResource(R.string.day_week_workout_screen_duration),
                value = toExercise.duration?.toReadableDuration(context) ?: ""
            )
        }

        if (isShowObservation(toExercise)) {
            LabeledText(
                modifier = Modifier.constrainAs(observationRef) {
                    when {
                        isShowDuration(toExercise) -> {
                            top.linkTo(durationRef.bottom, margin = 8.dp)
                        }

                        isShowSetsAndReps(toExercise) -> {
                            top.linkTo(setsAndRepsRef.bottom, margin = 8.dp)
                        }

                        else -> {
                            top.linkTo(exerciseRef.bottom, margin = 8.dp)
                        }
                    }

                    start.linkTo(parent.start, margin = 8.dp)
                    end.linkTo(parent.end, margin = 8.dp)

                    width = Dimension.fillToConstraints
                },
                label = stringResource(R.string.day_week_workout_screen_observation),
                value = toExercise.observation!!,
                maxLinesValue = 5
            )
        }

        HorizontalDivider(
            modifier = Modifier.constrainAs(dividerRef) {
                when {
                    isShowObservation(toExercise) -> {
                        top.linkTo(observationRef.bottom, margin = 8.dp)
                    }

                    isShowDuration(toExercise) -> {
                        top.linkTo(durationRef.bottom, margin = 8.dp)
                    }

                    isShowSetsAndReps(toExercise) -> {
                        top.linkTo(setsAndRepsRef.bottom, margin = 8.dp)
                    }

                    else -> {
                        top.linkTo(exerciseRef.bottom, margin = 8.dp)
                    }
                }

                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            color = MaterialTheme.colorScheme.outline
        )
    }
}

private fun isShowObservation(toExercise: TOExercise): Boolean {
    return !toExercise.observation.isNullOrEmpty()
}

private fun isShowDuration(toExercise: TOExercise): Boolean {
    return toExercise.duration != null
}

private fun isShowSetsAndReps(toExercise: TOExercise): Boolean {
    return toExercise.sets != null || toExercise.repetitions != null
}

@Composable
private fun getValueSetsAndReps(toExercise: TOExercise): String {
    return when {
        toExercise.sets != null && toExercise.repetitions != null -> {
            stringResource(
                R.string.day_week_workout_screen_sets_and_repetitions_value,
                toExercise.sets!!,
                toExercise.repetitions!!
            )
        }

        toExercise.sets != null -> {
            toExercise.sets.toString()
        }

        toExercise.repetitions != null -> {
            toExercise.repetitions.toString()
        }

        else -> ""
    }
}

@Composable
private fun getLabelSetsAndReps(toExercise: TOExercise): String {
    return when {
        toExercise.sets != null && toExercise.repetitions != null -> {
            stringResource(R.string.day_week_workout_screen_sets_and_repetitions)
        }

        toExercise.sets != null -> {
            stringResource(R.string.day_week_workout_screen_sets)
        }

        toExercise.repetitions != null -> {
            stringResource(R.string.day_week_workout_screen_repetitions)
        }

        else -> ""
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun GroupItemPreview() {
    FitnessProTheme {
        Surface {
            WorkoutGroupItem(dayWeekGroupItemEmpty)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun GroupItemPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            WorkoutGroupItem(dayWeekGroupItemEmpty)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekWorkoutItemPreview() {
    FitnessProTheme {
        Surface {
            DayWeekWorkoutItem(dayWeekItem1)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekWorkoutItemPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayWeekWorkoutItem(dayWeekItem1)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekWorkoutItem2Preview() {
    FitnessProTheme {
        Surface {
            DayWeekWorkoutItem(dayWeekItem2)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekWorkoutItem2PreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayWeekWorkoutItem(dayWeekItem2)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekWorkoutItem3Preview() {
    FitnessProTheme {
        Surface {
            DayWeekWorkoutItem(dayWeekItem3)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekWorkoutItem3PreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayWeekWorkoutItem(dayWeekItem3)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekWorkoutItem4Preview() {
    FitnessProTheme {
        Surface {
            DayWeekWorkoutItem(dayWeekItem4)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekWorkoutItem4PreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayWeekWorkoutItem(dayWeekItem4)
        }
    }
}