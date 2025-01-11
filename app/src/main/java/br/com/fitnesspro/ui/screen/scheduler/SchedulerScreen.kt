package br.com.fitnesspro.ui.screen.scheduler

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import br.com.fitnesspro.compose.components.buttons.fab.FitnessProFloatingActionButton
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
import br.com.fitnesspro.core.theme.RED_200
import br.com.fitnesspro.core.theme.RED_400
import br.com.fitnesspro.core.theme.RED_600
import br.com.fitnesspro.core.theme.RED_800
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.ui.navigation.CompromiseScreenArgs
import br.com.fitnesspro.ui.navigation.SchedulerDetailsScreenArgs
import br.com.fitnesspro.ui.screen.scheduler.callback.OnDayClick
import br.com.fitnesspro.ui.screen.scheduler.callback.OnNavigateToCompromise
import br.com.fitnesspro.ui.state.SchedulerUIState
import br.com.fitnesspro.ui.viewmodel.SchedulerViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun SchedulerScreen(
    viewModel: SchedulerViewModel,
    onBackClick: () -> Unit,
    onDayClick: OnDayClick,
    onNavigateToCompromise: OnNavigateToCompromise,
    onNavigateToConfig: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    SchedulerScreen(
        state = state,
        onBackClick = onBackClick,
        onDayClick = onDayClick,
        onNavigateToCompromise = onNavigateToCompromise,
        onNavigateToConfig = onNavigateToConfig,
        onUpdateSchedules = viewModel::updateSchedules
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerScreen(
    state: SchedulerUIState,
    onBackClick: () -> Unit = { },
    onDayClick: OnDayClick? = null,
    onNavigateToCompromise: OnNavigateToCompromise? = null,
    onNavigateToConfig: () -> Unit = { },
    onUpdateSchedules: () -> Unit = { }
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                showMenuWithLogout = false,
                onBackClick = onBackClick,
                actions = {
                    IconButtonConfig(onClick = onNavigateToConfig)
                }
            )
        },
        floatingActionButton = {
            if (state.isVisibleFabRecurrentScheduler) {
                FitnessProFloatingActionButton(
                    content = {
                        Icon(
                            painter = painterResource(br.com.fitnesspro.core.R.drawable.ic_edit_calendar_24dp),
                            contentDescription = stringResource(R.string.schedule_screen_fab_recurrent_description)
                        )
                    },
                    onClick = {
                        onNavigateToCompromise?.onExecute(
                            args = CompromiseScreenArgs(recurrent = true)
                        )
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->

        LaunchedEffect(Unit) {
            onUpdateSchedules()
        }

        ConstraintLayout(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val (headerRef, daysGridRef) = createRefs()

            SchedulerHeader(
                selectedYearMonth = state.selectedYearMonth,
                onBackClick =  state.onSelectYearMonth,
                onForwardClick = state.onSelectYearMonth,
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
                state = state,
                onDayClick = onDayClick
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
            textColor = GREY_800
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
    // Calcular o tamanho do intervalo e o tamanho de cada grupo
    val rangeSize = max - min + 1
    val groupSize = rangeSize / colors.size
    val remainder = rangeSize % colors.size

    // Criar os limites dos grupos
    val thresholds = mutableListOf<Int>()
    var currentMin = min
    for (i in colors.indices) {
        val size = if (i < remainder) groupSize + 1 else groupSize
        thresholds.add(currentMin + size - 1)
        currentMin += size
    }

    return thresholds
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
private fun DayCell(
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
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, radius = 20.dp, color = Color.Gray)
            ) {
                onDayClick?.onExecute(
                    args = SchedulerDetailsScreenArgs(scheduledDate = day)
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
            DaysGrid()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleScreenPreview() {
    FitnessProTheme {
        Surface {
            SchedulerScreen(
                state = SchedulerUIState(
                    title = "Agenda"
                )
            )
        }
    }
}