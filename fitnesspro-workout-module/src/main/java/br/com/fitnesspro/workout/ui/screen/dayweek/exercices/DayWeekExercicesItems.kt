package br.com.fitnesspro.workout.ui.screen.dayweek.exercices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.LabelGroupTextStyle
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.decorator.DayWeekExercicesGroupDecorator

@Composable
fun DayWeekWorkoutGroupItem(decorator: DayWeekExercicesGroupDecorator) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.inverseSurface)
            .padding(8.dp)
    ) {
        Text(
            modifier = Modifier.align(alignment = Alignment.Center),
            text = decorator.label,
            style = LabelGroupTextStyle,
            color = MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekWorkoutGroupItemPreview() {
    FitnessProTheme {
        Surface {
            DayWeekWorkoutGroupItem(dayWeekExercicesGroupDecorator)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekWorkoutGroupItemPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayWeekWorkoutGroupItem(dayWeekExercicesGroupDecorator)
        }
    }
}