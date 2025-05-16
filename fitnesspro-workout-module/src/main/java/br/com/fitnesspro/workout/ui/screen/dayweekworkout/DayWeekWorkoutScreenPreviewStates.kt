package br.com.fitnesspro.workout.ui.screen.dayweekworkout

import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.workout.ui.screen.dayweekworkout.decorator.WorkoutGroupDecorator
import br.com.fitnesspro.workout.ui.state.DayWeekWorkoutUIState
import java.time.Duration

internal val dayWeekGroupItemEmpty = WorkoutGroupDecorator(
    id = "1",
    label = "Aquecimento",
    items = emptyList()
)

internal val dayWeekItem1 = TOExercise(
    id = "1",
    name = "Esteira",
    duration = Duration.ofMinutes(30).toMillis(),
    sets = null,
    repetitions = null,
    rest = null,
    observation = "Se vier para a academia caminhando não é necessário fazer esse aquecimento"
)

internal val dayWeekItem2 = TOExercise(
    id = "1",
    name = "Manguito Rotador na Polia",
    duration = null,
    sets = 4,
    repetitions = 12,
    rest = 45,
    observation = "Alternar entre os ombros e utilizar cargas reduzidas"
)

internal val dayWeekItem3 = TOExercise(
    id = "1",
    name = "Supino Inclinado com Halteres",
    duration = null,
    sets = 4,
    repetitions = 12,
    rest = 45,
    observation = null
)

internal val dayWeekWorkoutScreenDefaultState = DayWeekWorkoutUIState(
    title = "Treino de Segunda",
    subtitle = "Peito, Ombro e Tríceps",
    dayWeekWorkoutGroups = listOf(
        WorkoutGroupDecorator(
            id = "1",
            label = "Aquecimento",
            items = listOf(
                TOExercise(
                    id = "1",
                    name = "Esteira",
                    duration = Duration.ofMinutes(30).toMillis(),
                    sets = null,
                    repetitions = null,
                    rest = null,
                    observation = "Se vier para a academia caminhando não é necessário fazer esse aquecimento"
                ),
                TOExercise(
                    id = "2",
                    name = "Manguito Rotador na Polia",
                    duration = null,
                    sets = 4,
                    repetitions = 12,
                    rest = null,
                    observation = "Alternar entre os ombros e utilizar cargas reduzidas"
                )
            )
        ),
        WorkoutGroupDecorator(
            id = "2",
            label = "Peito",
            items = listOf(
                TOExercise(
                    id = "1",
                    name = "Supino Inclinado com Halteres",
                    duration = null,
                    sets = 4,
                    repetitions = 12,
                    rest = 45,
                    observation = "Segurar embaixo por 2 segundos em toda repetição"
                ),
                TOExercise(
                    id = "2",
                    name = "Crussifixo Reto com Halteres",
                    duration = null,
                    sets = 4,
                    repetitions = 12,
                    rest = 45,
                    observation = null
                )
            )
        )
    ),
)