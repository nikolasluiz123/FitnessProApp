package br.com.fitnesspro.scheduler.ui.screen.scheduler.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.android.ui.compose.components.styles.LabelCalendarWeekTextStyle
import br.com.core.utils.extensions.getShortDisplayNameAllCaps
import br.com.fitnesspro.core.theme.FitnessProTheme
import java.time.DayOfWeek

@Composable
internal fun DaysOfWeekHeader() {
    val daysOfWeek = DayOfWeek.entries
        .let { it.subList(6, it.size) + it.subList(0, 6) }
        .map(DayOfWeek::getShortDisplayNameAllCaps)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                text = dayOfWeek,
                style = LabelCalendarWeekTextStyle,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .width(48.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun DaysOfWeekHeaderPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DaysOfWeekHeader()
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun DaysOfWeekHeaderPreviewLight() {
    FitnessProTheme {
        Surface {
            DaysOfWeekHeader()
        }
    }
}