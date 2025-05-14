package br.com.fitnesspro.workout.ui.screen.currentworkout

import br.com.fitnesspro.workout.ui.screen.currentworkout.decorator.CurrentWorkoutItemDecorator
import br.com.fitnesspro.workout.ui.state.CurrentWorkoutUIState
import java.time.DayOfWeek

internal val currentWorkoutEmptyState = CurrentWorkoutUIState(
    title = "Treino Atual",
    subtitle = "01/05/2024 até 01/07/2024"
)

internal val currentWorkoutState = CurrentWorkoutUIState(
    title = "Treino Atual",
    subtitle = "01/05/2024 até 01/07/2024",
    items = listOf(
        CurrentWorkoutItemDecorator(
            dayWeek = DayOfWeek.MONDAY,
            muscularGroups = "Peito, Ombro e Tríceps"
        ),
        CurrentWorkoutItemDecorator(
            dayWeek = DayOfWeek.WEDNESDAY,
            muscularGroups = "Costas, Ombro e Bíceps"
        ),
        CurrentWorkoutItemDecorator(
            dayWeek = DayOfWeek.FRIDAY,
            muscularGroups = "Perna Completa"
        )
    )
)

internal val currentWorkoutItemDecorator = CurrentWorkoutItemDecorator(
    dayWeek = DayOfWeek.MONDAY,
    muscularGroups = "Peito, Costas, Ombros"
)