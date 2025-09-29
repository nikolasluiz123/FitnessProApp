package br.com.fitnesspro.compose.components.fields

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.android.ui.compose.components.fields.weekselector.DayWeeksSelector
import br.com.android.ui.compose.components.fields.weekselector.DayWeeksSelectorField
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.RED_400
import java.time.DayOfWeek

@Composable
fun FitnessProDayWeeksSelector(
    selectorField: DayWeeksSelectorField,
    modifier: Modifier = Modifier
) {
    DayWeeksSelector(
        selectorField = selectorField,
        selectedDaysColor = RED_400,
        modifier = modifier
    )
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun DayWeeksSelectorPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProDayWeeksSelector(
                selectorField = DayWeeksSelectorField(
                    selected = mutableListOf(),
                    onSelect = { }
                )
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun DayWeeksSelectorWithSelectedValuesPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProDayWeeksSelector(
                selectorField = DayWeeksSelectorField(
                    selected = mutableListOf(DayOfWeek.FRIDAY, DayOfWeek.WEDNESDAY),
                    onSelect = { }
                )
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun DayWeeksSelectorPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProDayWeeksSelector(
                selectorField = DayWeeksSelectorField(
                    selected = mutableListOf(),
                    onSelect = { }
                )
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun DayWeeksSelectorWithSelectedValuesPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProDayWeeksSelector(
                selectorField = DayWeeksSelectorField(
                    selected = mutableListOf(DayOfWeek.FRIDAY, DayOfWeek.WEDNESDAY),
                    onSelect = { }
                )
            )
        }
    }
}