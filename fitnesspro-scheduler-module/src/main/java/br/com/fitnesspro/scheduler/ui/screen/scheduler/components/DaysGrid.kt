package br.com.fitnesspro.scheduler.ui.screen.scheduler.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.LabelCalendarDayTextStyle
import br.com.fitnesspro.core.theme.RED_200
import br.com.fitnesspro.core.theme.RED_400
import br.com.fitnesspro.core.theme.RED_600
import br.com.fitnesspro.core.theme.RED_800
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.ui.screen.scheduler.callback.OnDayClick
import br.com.fitnesspro.scheduler.ui.state.SchedulerUIState
import java.time.LocalDate

@Composable
internal fun DaysGrid(
    modifier: Modifier = Modifier,
    state: SchedulerUIState = SchedulerUIState(),
    onDayClick: OnDayClick? = null
) {
    AnimatedContent(
        modifier = modifier,
        targetState = state.selectedYearMonth,
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> if (targetState > initialState) fullWidth else -fullWidth }
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { fullWidth -> if (targetState > initialState) -fullWidth else fullWidth }
            ) using SizeTransform(clip = false)
        },
        label = "AnimatedDaysGrid"
    ) { animatedYearMonth ->

        val firstDayOfMonth = LocalDate.of(animatedYearMonth.year, animatedYearMonth.month, 1)
        val totalDays = animatedYearMonth.lengthOfMonth()
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val days = (1..totalDays).map { LocalDate.of(animatedYearMonth.year, animatedYearMonth.month, it) }

        val paddedDays = List(firstDayOfWeek) { null } + days
        val totalCells = ((paddedDays.size + 6) / 7) * 7
        val fullyPaddedDays = paddedDays + List(totalCells - paddedDays.size) { null }

        val weeks = fullyPaddedDays.chunked(7)

        Column {
            DaysOfWeekHeader()

            weeks.forEach { week ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    week.forEach { day ->
                        if (day == null) {
                            Spacer(
                                modifier = Modifier
                                    .size(48.dp)
                                    .aspectRatio(1f)
                            )
                        } else {
                            getDayStyle(day, state)?.let { style ->
                                DayCell(
                                    day = day,
                                    style = style,
                                    onDayClick = onDayClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getDayStyle(day: LocalDate, state: SchedulerUIState): DayStyle? {
    val scheduleForDay = state.schedules.firstOrNull { it.date == day }

    return if (scheduleForDay != null) {
        when (state.userType) {
            EnumUserType.PERSONAL_TRAINER,
            EnumUserType.NUTRITIONIST -> {
                val count = scheduleForDay.count
                val min = state.toSchedulerConfig?.minScheduleDensity!!
                val max = state.toSchedulerConfig.maxScheduleDensity!!
                val colors = listOf(RED_200, RED_400, RED_600, RED_800)
                val thresholds = getThresholds(max, min, colors)
                val backgroundColor = getDayBackgroundColor(count, thresholds, colors)

                DayStyle(
                    backgroundColor = backgroundColor,
                    textStyle = LabelCalendarDayTextStyle,
                    textColor = Color.White
                )
            }

            EnumUserType.ACADEMY_MEMBER -> {
                DayStyle(
                    backgroundColor = RED_200,
                    textStyle = LabelCalendarDayTextStyle,
                    textColor = Color.White
                )
            }

            null -> null
        }

    } else {
        DayStyle(
            backgroundColor = Color.Transparent,
            textStyle = LabelCalendarDayTextStyle,
            textColor = MaterialTheme.colorScheme.onBackground
        )
    }
}

private fun getDayBackgroundColor(
    count: Int,
    thresholds: MutableList<Int>,
    colors: List<Color>
): Color {
    return when {
        count <= thresholds[0] -> colors[0]
        count <= thresholds[1] -> colors[1]
        count <= thresholds[2] -> colors[2]
        else -> colors[3]
    }
}

private fun getThresholds(
    max: Int,
    min: Int,
    colors: List<Color>
): MutableList<Int> {
    val rangeSize = max - min + 1
    val groupSize = rangeSize / colors.size
    val remainder = rangeSize % colors.size

    val thresholds = mutableListOf<Int>()
    var currentMin = min
    for (i in colors.indices) {
        val size = if (i < remainder) groupSize + 1 else groupSize
        thresholds.add(currentMin + size - 1)
        currentMin += size
    }

    return thresholds
}

@Preview(device = "id:small_phone")
@Composable
private fun DaysGridPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DaysGrid()
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DaysGridPreviewLight() {
    FitnessProTheme {
        Surface {
            DaysGrid()
        }
    }
}