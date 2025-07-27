package br.com.fitnesspro.scheduler.ui.screen.scheduler.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.scheduler.ui.navigation.SchedulerDetailsScreenArgs
import br.com.fitnesspro.scheduler.ui.screen.scheduler.callback.OnDayClick
import br.com.fitnesspro.scheduler.ui.screen.scheduler.defaultDayStyle
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTags.SCHEDULER_SCREEN_DAY_CELL
import java.time.LocalDate
import java.time.ZoneId

@Composable
internal fun DayCell(
    day: LocalDate,
    style: DayStyle,
    onDayClick: OnDayClick? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        Modifier
            .padding(4.dp)
            .size(40.dp)
            .background(color = style.backgroundColor, shape = CircleShape)
            .testTag(SCHEDULER_SCREEN_DAY_CELL.name)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, radius = 20.dp, color = Color.Gray)
            ) {
                onDayClick?.onExecute(
                    args = SchedulerDetailsScreenArgs(
                        scheduledDate = day
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            style = style.textStyle,
            color = style.textColor
        )
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun DayCellPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayCell(
                day = dateNow(ZoneId.systemDefault()),
                style = defaultDayStyle
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun DayCellPreviewLight() {
    FitnessProTheme {
        Surface {
            DayCell(
                day = dateNow(ZoneId.systemDefault()),
                style = defaultDayStyle
            )
        }
    }
}