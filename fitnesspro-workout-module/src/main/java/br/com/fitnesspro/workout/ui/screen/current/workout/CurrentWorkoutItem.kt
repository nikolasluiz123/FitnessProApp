package br.com.fitnesspro.workout.ui.screen.current.workout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.ui.screen.current.workout.decorator.CurrentWorkoutDecorator
import br.com.fitnesspro.workout.R

@Composable
fun CurrentWorkoutItem(decorator: CurrentWorkoutDecorator) {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
    ) {
        val (dayWeekRef, muscularGroupRef, dividerRef) = createRefs()

        createHorizontalChain(dayWeekRef, muscularGroupRef)

        LabeledText(
            modifier = Modifier
                .padding(start = 8.dp)
                .constrainAs(dayWeekRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top, margin = 8.dp)

                    width = Dimension.fillToConstraints
                },
            label = stringResource(R.string.current_workout_item_label_day_week),
            value = decorator.dayWeek.getFirstPartFullDisplayName(),
        )

        LabeledText(
            modifier = Modifier
                .padding(end = 8.dp)
                .constrainAs(muscularGroupRef) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top, margin = 8.dp)

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
            },
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CurrentWorkoutItemPreview() {
    FitnessProTheme {
        Surface {
            CurrentWorkoutItem(
                decorator = currentWorkoutItemDecorator
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CurrentWorkoutItemPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            CurrentWorkoutItem(
                decorator = currentWorkoutItemDecorator
            )
        }
    }
}