package br.com.fitnesspro.workout.ui.state.reports

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
import br.com.fitnesspro.compose.components.divider.FitnessProHorizontalDivider
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.ValueTextStyle
import br.com.fitnesspro.tuple.WorkoutTuple

@Composable
internal fun WorkoutDialogListItem(
    workout: WorkoutTuple,
    onItemClick: (WorkoutTuple) -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(workout)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(12.dp),
            text = "${workout.dateStart.format(DATE)} - ${workout.dateEnd.format(DATE)}",
            style = ValueTextStyle.copy(fontSize = 16.sp)
        )
    }

    FitnessProHorizontalDivider()
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun PersonDialogListItemPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            WorkoutDialogListItem(
                workout = defaultWorkoutTuple
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun PersonDialogListItemPreviewLight() {
    FitnessProTheme {
        Surface {
            WorkoutDialogListItem(
                workout = defaultWorkoutTuple
            )
        }
    }
}