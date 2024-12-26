package br.com.fitnesspro.ui.screen.schedule

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonConfig
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.getShortDisplayNameAllCaps
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_800
import br.com.fitnesspro.core.theme.LabelCalendarDayTextStyle
import br.com.fitnesspro.core.theme.LabelCalendarWeekTextStyle
import br.com.fitnesspro.core.theme.LabelTextStyle
import br.com.fitnesspro.core.theme.RED_400
import br.com.fitnesspro.ui.state.ScheduleUIState
import br.com.fitnesspro.ui.viewmodel.ScheduleViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ScheduleScreen(
        state = state,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    state: ScheduleUIState,
    onBackClick: () -> Unit = { },
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                showMenuWithLogout = false,
                onBackClick = onBackClick,
                actions = {
                    IconButtonConfig(
                        onClick = {
                            // TODO - Navegar para tela de configurações
                        }
                    )
                }
            )
        }
    ) { padding ->
        ConstraintLayout(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val (headerRef, daysGridRef) = createRefs()
            var selectedYearMonth by remember { mutableStateOf(YearMonth.now()) }

            SchedulerHeader(
                selectedYearMonth = selectedYearMonth,
                onBackClick = { selectedYearMonth = it },
                onForwardClick = { selectedYearMonth = it },
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(headerRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)

                        width = Dimension.fillToConstraints
                    }
            )

            DaysGrid(
                modifier = Modifier
                    .constrainAs(daysGridRef) {
                    top.linkTo(headerRef.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                yearMonth = selectedYearMonth
            )
        }

    }
}

@Composable
private fun SchedulerHeader(
    selectedYearMonth: YearMonth,
    onBackClick: (newYearMonth: YearMonth) -> Unit,
    onForwardClick: (newYearMonth: YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier) {
        val (monthRef, backRef, forwardRef) = createRefs()

        IconButton(
            modifier = Modifier.constrainAs(backRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            onClick = {
                onBackClick(selectedYearMonth.minusMonths(1))
            }
        ) {
            Icon(
                painter = painterResource(br.com.fitnesspro.core.R.drawable.ic_navigate_back_16dp),
                contentDescription = stringResource(R.string.schedule_screen_navigate_back_description)
            )
        }

        Text(
            modifier = Modifier.constrainAs(monthRef) {
                start.linkTo(backRef.end)
                end.linkTo(forwardRef.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            text = selectedYearMonth.format(EnumDateTimePatterns.MONTH_YEAR),
            style = LabelTextStyle,
            color = GREY_800
        )

        IconButton(
            modifier = Modifier.constrainAs(forwardRef) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            onClick = {
                onForwardClick(selectedYearMonth.plusMonths(1))
            }
        ) {
            Icon(
                painter = painterResource(br.com.fitnesspro.core.R.drawable.ic_navigate_forward_16dp),
                contentDescription = stringResource(R.string.schedule_screen_navigate_forward_description)
            )
        }
    }
}

@Composable
private fun DaysGrid(
    yearMonth: YearMonth,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        modifier = modifier,
        targetState = yearMonth,
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
                            DayCell(
                                day = day,
                                style = DayStyle(
                                    backgroundColor = RED_400,
                                    textStyle = LabelCalendarDayTextStyle,
                                    textColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DaysOfWeekHeader() {
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
                color = GREY_800,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DayCell(day: LocalDate, style: DayStyle) {
    Box(
        Modifier
            .padding(4.dp)
            .size(40.dp)
            .background(color = style.backgroundColor, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            style = style.textStyle,
            color = style.textColor
        )
    }
}

data class DayStyle(
    val backgroundColor: Color,
    val textColor: Color,
    val textStyle: TextStyle
)

@Preview
@Composable
private fun DayCellPreview() {
    FitnessProTheme {
        Surface {
            DayCell(
                day = LocalDate.now(),
                style = DayStyle(
                    backgroundColor = RED_400,
                    textStyle = LabelCalendarDayTextStyle,
                    textColor = Color.White
                )
            )
        }
    }
}

@Preview
@Composable
private fun DaysGridPreview() {
    FitnessProTheme {
        Surface {
            DaysGrid(yearMonth = YearMonth.now())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleScreenPreview() {
    FitnessProTheme {
        Surface {
            ScheduleScreen(
                state = ScheduleUIState(
                    title = "Agenda"
                )
            )
        }
    }
}