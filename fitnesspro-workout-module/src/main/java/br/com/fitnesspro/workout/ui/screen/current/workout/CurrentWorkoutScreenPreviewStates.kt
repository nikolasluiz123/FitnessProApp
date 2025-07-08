package br.com.fitnesspro.workout.ui.screen.current.workout

import br.com.fitnesspro.workout.ui.screen.current.workout.decorator.CurrentWorkoutDecorator
import br.com.fitnesspro.workout.ui.state.CurrentWorkoutUIState
import java.time.DayOfWeek

internal val currentWorkoutEmptyState = CurrentWorkoutUIState(
    subtitle = "01/05/2024 até 01/07/2024"
)

internal val currentWorkoutState = CurrentWorkoutUIState(
    subtitle = "01/05/2024 até 01/07/2024",
    items = listOf(
        CurrentWorkoutDecorator(
            dayWeek = DayOfWeek.MONDAY,
            muscularGroups = "Peito, Ombro e Tríceps"
        ),
        CurrentWorkoutDecorator(
            dayWeek = DayOfWeek.WEDNESDAY,
            muscularGroups = "Costas, Ombro e Bíceps"
        ),
        CurrentWorkoutDecorator(
            dayWeek = DayOfWeek.FRIDAY,
            muscularGroups = "Perna Completa"
        )
    )
)

internal val currentWorkoutItemDecorator = CurrentWorkoutDecorator(
    dayWeek = DayOfWeek.MONDAY,
    muscularGroups = "Peito, Costas, Ombros"
)