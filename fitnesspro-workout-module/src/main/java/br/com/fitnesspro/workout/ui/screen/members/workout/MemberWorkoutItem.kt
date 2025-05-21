package br.com.fitnesspro.workout.ui.screen.members.workout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
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
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.workout.R

@Composable
fun MemberWorkoutItem(toWorkout: TOWorkout) {
    ConstraintLayout(Modifier.fillMaxWidth()) {
        val (nameRef, dateRef, lineRef) = createRefs()

        createHorizontalChain(nameRef, dateRef)

        LabeledText(
            modifier = Modifier
                .padding(start = 8.dp)
                .constrainAs(nameRef) {
                    top.linkTo(parent.top, margin = 8.dp)
                    start.linkTo(parent.start)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.6f
                },
            label = stringResource(R.string.members_workout_screen_item_label_name),
            value = toWorkout.memberName
        )

        LabeledText(
            modifier = Modifier
                .padding(end = 8.dp)
                .constrainAs(dateRef) {
                    top.linkTo(parent.top, margin = 8.dp)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.4f
                },
            label = stringResource(R.string.members_workout_screen_item_label_vencimento),
            value = toWorkout.dateEnd?.format(EnumDateTimePatterns.DATE)!!,
            textAlign = TextAlign.End
        )

        HorizontalDivider(
            modifier = Modifier.constrainAs(lineRef) {
                top.linkTo(nameRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            }
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun MemberWorkoutItemPreview() {
    FitnessProTheme {
        Surface {
            MemberWorkoutItem(
                toWorkout = defaultMemberWorkoutItem
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun MemberWorkoutItemPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            MemberWorkoutItem(
                toWorkout = defaultMemberWorkoutItem
            )
        }
    }
}