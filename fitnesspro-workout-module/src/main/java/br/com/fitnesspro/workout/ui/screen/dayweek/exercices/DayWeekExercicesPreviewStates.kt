package br.com.fitnesspro.workout.ui.screen.dayweek.exercices

import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.decorator.DayWeekExercicesGroupDecorator
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.decorator.WorkoutGroupDecorator
import br.com.fitnesspro.workout.ui.state.DayWeekExercisesUIState
import java.util.UUID

internal val defaultDayWeekExercisesGroupsList = listOf(
    DayWeekExercicesGroupDecorator(
        id = UUID.randomUUID().toString(),
        label = "Segunda",
        items = listOf(
            WorkoutGroupDecorator(
                id = UUID.randomUUID().toString(),
                label = "Aquecimento",
                items = listOf(
                    TOExercise(
                        id = UUID.randomUUID().toString(),
                        name = "Esteira",
                        duration = 1800000,
                    )
                )
            ),
            WorkoutGroupDecorator(
                id = UUID.randomUUID().toString(),
                label = "Peito",
                items = listOf(
                    TOExercise(
                        id = UUID.randomUUID().toString(),
                        name = "Supino Inclinado com Halteres",
                        sets = 4,
                        repetitions = 12,
                        observation = "Segurar embaixo por 2 segundos em toda repetição"
                    )
                )
            )
        )
    ),
    DayWeekExercicesGroupDecorator(
        id = UUID.randomUUID().toString(),
        label = "Terça",
        items = listOf(
            WorkoutGroupDecorator(
                id = UUID.randomUUID().toString(),
                label = "Aquecimento",
                items = listOf(
                    TOExercise(
                        id = UUID.randomUUID().toString(),
                        name = "Esteira",
                        duration = 1800000,
                    )
                )
            ),
            WorkoutGroupDecorator(
                id = UUID.randomUUID().toString(),
                label = "Costas",
                items = listOf(
                    TOExercise(
                        id = UUID.randomUUID().toString(),
                        name = "Remada Unilateral com Halters",
                        sets = 4,
                        repetitions = 12,
                    )
                )
            )
        )
    )
)

internal val dayWeekExercicesGroupDecorator = DayWeekExercicesGroupDecorator(
    id = UUID.randomUUID().toString(),
    label = "Segunda",
    items = emptyList()
)

internal val dayWeekExercisesDefaultEmptyState = DayWeekExercisesUIState(
    title = "Treino do Nikolas Luiz Schmitt",
    subtitle = "01/05/2024 até 01/07/2024",
)
internal val dayWeekExercisesDefaultState = DayWeekExercisesUIState(
    title = "Treino do Nikolas Luiz Schmitt",
    subtitle = "01/05/2024 até 01/07/2024",
    groups = defaultDayWeekExercisesGroupsList
)

internal val dayWeekExercisesOverDueState = DayWeekExercisesUIState(
    title = "Treino do Nikolas Luiz Schmitt",
    subtitle = "Vencido em 01/07/2024",
    isOverDue = true,
    groups = defaultDayWeekExercisesGroupsList
)
