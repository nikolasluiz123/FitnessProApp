package br.com.fitnesspro.workout.ui.screen.exercice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.ValueTextStyle
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOWorkoutGroup

@Composable
fun ExercisePagedDialogItem(toExercise: TOExercise, onItemClick: (TOExercise) -> Unit = { }) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(toExercise) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = toExercise.name!!,
            style = ValueTextStyle.copy(fontSize = 16.sp)
        )
    }

    HorizontalDivider()
}

@Composable
fun GroupDialogItem(toWorkoutGroup: TOWorkoutGroup, onItemClick: (TOWorkoutGroup) -> Unit = { }) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(toWorkoutGroup) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = toWorkoutGroup.name!!,
            style = ValueTextStyle.copy(fontSize = 16.sp)
        )
    }

    HorizontalDivider()
}

@Preview(device = "id:small_phone")
@Composable
private fun ExercisePagedDialogItemPreview() {
    FitnessProTheme {
        Surface {
            ExercisePagedDialogItem(
                toExercise = pagedDialogExerciseItem
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun GroupDialogItemPreview() {
    FitnessProTheme {
        Surface {
            GroupDialogItem(
                toWorkoutGroup = dialogGroupItem
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ExercisePagedDialogItemPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExercisePagedDialogItem(
                toExercise = pagedDialogExerciseItem
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun GroupDialogItemPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            GroupDialogItem(
                toWorkoutGroup = dialogGroupItem
            )
        }
    }
}