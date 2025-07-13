package br.com.fitnesspro.workout.ui.screen.details

import br.com.fitnesspro.compose.components.fields.state.TabState
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.dayWeekItem3
import br.com.fitnesspro.workout.ui.screen.details.enums.EnumTabsExerciseDetailsScreen
import br.com.fitnesspro.workout.ui.state.ExerciseDetailsUIState

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