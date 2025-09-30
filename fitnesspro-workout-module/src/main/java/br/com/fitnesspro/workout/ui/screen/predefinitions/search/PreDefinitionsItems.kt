package br.com.fitnesspro.workout.ui.screen.predefinitions.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import br.com.android.ui.compose.components.divider.BaseHorizontalDivider
import br.com.android.ui.compose.components.label.LabeledText
import br.com.android.ui.compose.components.styles.LabelGroupTextStyle
import br.com.core.android.utils.extensions.toReadableDuration
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.tuple.ExercisePredefinitionGroupedTuple
import br.com.fitnesspro.workout.R

@Composable
fun ExercisePreDefinitionItem(
    predefinition: ExercisePredefinitionGroupedTuple,
    modifier: Modifier = Modifier,
    onItemClick: (ExercisePredefinitionGroupedTuple) -> Unit = { },
) {
    val context = LocalContext.current

    ConstraintLayout(
        modifier
            .fillMaxWidth()
            .clickable { onItemClick(predefinition) }
    ) {
        val (exerciseRef, durationRef, setsAndRepsRef, restRef, dividerRef) = createRefs()

        LabeledText(
            modifier = Modifier.constrainAs(exerciseRef) {
                top.linkTo(parent.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)

                width = Dimension.fillToConstraints
            },
            label = stringResource(R.string.exercise_pre_definition_screen_exercise),
            value = predefinition.name ?: ""
        )

        if (isShowSetsAndReps(predefinition)) {
            LabeledText(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .constrainAs(setsAndRepsRef) {
                        top.linkTo(exerciseRef.bottom, margin = 8.dp)
                        start.linkTo(parent.start)

                        width = Dimension.fillToConstraints
                    },
                label = getLabelSetsAndReps(predefinition),
                value = getValueSetsAndReps(predefinition)
            )

            LabeledText(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .constrainAs(restRef) {
                        top.linkTo(exerciseRef.bottom, margin = 8.dp)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                    },
                label = stringResource(R.string.exercise_pre_definition_screen_rest),
                value = predefinition.rest?.toReadableDuration(context) ?: stringResource(R.string.exercise_pre_definition_screen_no_rest),
                textAlign = TextAlign.End
            )

            createHorizontalChain(setsAndRepsRef, restRef)
        }

        if (isShowDuration(predefinition)) {
            LabeledText(
                modifier = Modifier
                    .constrainAs(durationRef) {
                        if (isShowSetsAndReps(predefinition)) {
                            top.linkTo(setsAndRepsRef.bottom, margin = 8.dp)
                        } else {
                            top.linkTo(exerciseRef.bottom, margin = 8.dp)
                        }

                        start.linkTo(parent.start, margin = 8.dp)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                    },
                label = stringResource(R.string.exercise_pre_definition_screen_duration),
                value = predefinition.duration?.toReadableDuration(context) ?: ""
            )
        }

        BaseHorizontalDivider(
            modifier = Modifier.constrainAs(dividerRef) {
                when {
                    isShowDuration(predefinition) -> {
                        top.linkTo(durationRef.bottom, margin = 8.dp)
                    }

                    isShowSetsAndReps(predefinition) -> {
                        top.linkTo(setsAndRepsRef.bottom, margin = 8.dp)
                    }

                    else -> {
                        top.linkTo(exerciseRef.bottom, margin = 8.dp)
                    }
                }

                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
        )
    }
}

@Composable
fun PreDefinitionGroupItem(
    predefinition: ExercisePredefinitionGroupedTuple,
    onItemClick: (ExercisePredefinitionGroupedTuple) -> Unit = {},
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .clickable(
                onClick = {
                    onItemClick(predefinition)
                },
                enabled = predefinition.id != null
            )
            .padding(8.dp)
    ) {
        Text(
            modifier = Modifier.align(alignment = Alignment.Center),
            text = predefinition.groupName ?: stringResource(R.string.exercise_pre_definition_no_workout_group),
            style = LabelGroupTextStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun isShowDuration(predefinition: ExercisePredefinitionGroupedTuple): Boolean {
    return predefinition.duration != null
}

private fun isShowSetsAndReps(predefinition: ExercisePredefinitionGroupedTuple): Boolean {
    return predefinition.sets != null || predefinition.reps != null
}

@Composable
private fun getValueSetsAndReps(predefinition: ExercisePredefinitionGroupedTuple): String {
    return when {
        predefinition.sets != null && predefinition.reps != null -> {
            stringResource(
                R.string.day_week_workout_screen_sets_and_repetitions_value,
                predefinition.sets!!,
                predefinition.reps!!
            )
        }

        predefinition.sets != null -> {
            predefinition.sets.toString()
        }

        predefinition.reps != null -> {
            predefinition.reps.toString()
        }

        else -> ""
    }
}

@Composable
private fun getLabelSetsAndReps(predefinition: ExercisePredefinitionGroupedTuple): String {
    return when {
        predefinition.sets != null && predefinition.reps != null -> {
            stringResource(R.string.day_week_workout_screen_sets_and_repetitions)
        }

        predefinition.sets != null -> {
            stringResource(R.string.day_week_workout_screen_sets)
        }

        predefinition.reps != null -> {
            stringResource(R.string.day_week_workout_screen_repetitions)
        }

        else -> ""
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExercisePreDefinitionItemPreview() {
    FitnessProTheme {
        Surface {
            ExercisePreDefinitionItem(
                predefinition = predefinitionState
            )
        }
    }
}


@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun PreDefinitionGroupItemPreview() {
    FitnessProTheme {
        Surface {
            PreDefinitionGroupItem(
                predefinition = predefinitionGroupState
            )
        }
    }
}


@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExercisePreDefinitionItemDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExercisePreDefinitionItem(
                predefinition = predefinitionState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun PreDefinitionGroupItemDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            PreDefinitionGroupItem(
                predefinition = predefinitionGroupState
            )
        }
    }
}