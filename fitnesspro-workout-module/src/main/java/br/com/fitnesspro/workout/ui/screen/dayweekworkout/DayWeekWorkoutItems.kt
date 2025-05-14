package br.com.fitnesspro.workout.ui.screen.dayweekworkout

import androidx.compose.foundation.background
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
import br.com.fitnesspro.core.extensions.toDurationFormatted
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.LabelGroupTextStyle
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.dayweekworkout.decorator.DayWeekWorkoutGroupDecorator
import br.com.fitnesspro.workout.ui.screen.dayweekworkout.decorator.DayWeekWorkoutItemDecorator

@Composable
fun DayWeekWorkoutGroupItem(groupDecorator: DayWeekWorkoutGroupDecorator) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        Text(
            modifier = Modifier.align(alignment = Alignment.Center),
            text = groupDecorator.label,
            style = LabelGroupTextStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DayWeekWorkoutItem(decorator: DayWeekWorkoutItemDecorator) {
    val context = LocalContext.current

    ConstraintLayout(
        Modifier
            .fillMaxWidth()
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
            value = decorator.exercise
        )

        if (decorator.duration != null) {
            createHorizontalChain(durationRef, restRef)

            LabeledText(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .constrainAs(durationRef) {
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
                modifier = Modifier
                    .padding(start = 8.dp)
                    .constrainAs(setsAndRepsRef) {
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
            modifier = Modifier
                .padding(end = 8.dp)
                .constrainAs(restRef) {
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

                    start.linkTo(parent.start, margin = 8.dp)
                    end.linkTo(parent.end, margin = 8.dp)

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
            DayWeekWorkoutGroupItem(dayWeekGroupItemEmpty)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun GroupItemPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayWeekWorkoutGroupItem(dayWeekGroupItemEmpty)
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