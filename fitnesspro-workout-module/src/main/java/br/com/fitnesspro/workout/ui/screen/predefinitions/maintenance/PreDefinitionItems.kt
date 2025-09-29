package br.com.fitnesspro.workout.ui.screen.predefinitions.maintenance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.android.ui.compose.components.divider.BaseHorizontalDivider
import br.com.android.ui.compose.components.styles.ValueTextStyle

import br.com.fitnesspro.core.theme.FitnessProTheme

import br.com.fitnesspro.to.TOExercisePreDefinition
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition

@Composable
fun ExercisePreDefinitionPagedDialogItem(
    toExercisePreDefinition: TOExercisePreDefinition,
    onItemClick: (TOExercisePreDefinition) -> Unit = { }
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(toExercisePreDefinition) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = toExercisePreDefinition.name!!,
            style = ValueTextStyle.copy(fontSize = 16.sp)
        )
    }

    BaseHorizontalDivider()
}

@Composable
fun WorkoutGroupPreDefinitionPagedDialogItem(
    toWorkoutGroupPreDefinition: TOWorkoutGroupPreDefinition,
    onItemClick: (TOWorkoutGroupPreDefinition) -> Unit = { }
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(toWorkoutGroupPreDefinition) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = toWorkoutGroupPreDefinition.name!!,
            style = ValueTextStyle.copy(fontSize = 16.sp)
        )
    }

    BaseHorizontalDivider()
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExercisePreDefinitionPagedDialogItemPreview() {
    FitnessProTheme {
        Surface {
            ExercisePreDefinitionPagedDialogItem(
                toExercisePreDefinition = pagedDialogExercisePreDefinitionItem
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun WorkoutGroupPreDefinitionPagedDialogItemPreview() {
    FitnessProTheme {
        Surface {
            WorkoutGroupPreDefinitionPagedDialogItem(
                toWorkoutGroupPreDefinition = pagedDialogWorkoutGroupPreDefinitionItem
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExercisePreDefinitionPagedDialogItemDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExercisePreDefinitionPagedDialogItem(
                toExercisePreDefinition = pagedDialogExercisePreDefinitionItem
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun WorkoutGroupPreDefinitionPagedDialogItemDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            WorkoutGroupPreDefinitionPagedDialogItem(
                toWorkoutGroupPreDefinition = pagedDialogWorkoutGroupPreDefinitionItem
            )
        }
    }
}
