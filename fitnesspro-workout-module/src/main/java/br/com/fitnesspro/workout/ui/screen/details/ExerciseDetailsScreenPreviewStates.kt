package br.com.fitnesspro.workout.ui.screen.details

import br.com.fitnesspro.compose.components.fields.state.TabState
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.tuple.ExerciseExecutionGroupedTuple
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.dayWeekItem3
import br.com.fitnesspro.workout.ui.screen.details.enums.EnumTabsExerciseDetailsScreen
import br.com.fitnesspro.workout.ui.state.ExerciseDetailsUIState
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

internal val defaultExerciseDetailsUIState = ExerciseDetailsUIState(
    subtitle = "Supino Inclinado com Halteres",
    tabState = TabState(
        tabs = mutableListOf(
            Tab(
                enum = EnumTabsExerciseDetailsScreen.ORIENTATION,
                selected = true,
                enabled = true
            ),
            Tab(
                enum = EnumTabsExerciseDetailsScreen.EVOLUTION,
                selected = false,
                enabled = false
            )
        )
    ),
    toExercise = dayWeekItem3
)

internal val exerciseExecution1 = ExerciseExecutionGroupedTuple(
    duration = Duration.ofMinutes(10).toMillis(),
    durationUnit = ChronoUnit.MINUTES,
    date = LocalDateTime.now(),
)

internal val exerciseExecution2 = ExerciseExecutionGroupedTuple(
    duration = Duration.ofMinutes(1).toMillis(),
    durationUnit = ChronoUnit.MINUTES,
    rest = Duration.ofSeconds(45).toMillis(),
    restUnit = ChronoUnit.SECONDS,
    date = LocalDateTime.now(),
)

internal val exerciseExecution3 = ExerciseExecutionGroupedTuple(
    rest = Duration.ofSeconds(45).toMillis(),
    restUnit = ChronoUnit.SECONDS,
    weight = 40.5,
    repetitions = 12,
    date = LocalDateTime.now(),
)