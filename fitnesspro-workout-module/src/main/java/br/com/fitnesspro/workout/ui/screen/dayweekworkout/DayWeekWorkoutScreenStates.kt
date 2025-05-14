package br.com.fitnesspro.workout.ui.screen.dayweekworkout

import br.com.fitnesspro.workout.ui.screen.dayweekworkout.decorator.DayWeekWorkoutGroupDecorator
import br.com.fitnesspro.workout.ui.screen.dayweekworkout.decorator.DayWeekWorkoutItemDecorator
import br.com.fitnesspro.workout.ui.state.DayWeekWorkoutUIState
import java.time.LocalTime

internal val dayWeekGroupItemEmpty = DayWeekWorkoutGroupDecorator(
    id = "1",
    label = "Aquecimento",
    items = emptyList()
)

internal val dayWeekItem1 = DayWeekWorkoutItemDecorator(
    id = "1",
    exercise = "Esteira",
    duration = LocalTime.of(0, 30).toSecondOfDay().toLong(),
    sets = null,
    repetitions = null,
    rest = null,
    observation = "Se vier para a academia caminhando não é necessário fazer esse aquecimento"
)

internal val dayWeekItem2 = DayWeekWorkoutItemDecorator(
    id = "1",
    exercise = "Manguito Rotador na Polia",
    duration = null,
    sets = 4,
    repetitions = 12,
    rest = 45,
    observation = "Alternar entre os ombros e utilizar cargas reduzidas"
)

internal val dayWeekItem3 = DayWeekWorkoutItemDecorator(
    id = "1",
    exercise = "Supino Inclinado com Halteres",
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
        DayWeekWorkoutGroupDecorator(
            id = "1",
            label = "Aquecimento",
            items = listOf(
                DayWeekWorkoutItemDecorator(
                    id = "1",
                    exercise = "Esteira",
                    duration = LocalTime.of(0, 30).toSecondOfDay().toLong(),
                    sets = null,
                    repetitions = null,
                    rest = null,
                    observation = "Se vier para a academia caminhando não é necessário fazer esse aquecimento"
                ),
                DayWeekWorkoutItemDecorator(
                    id = "2",
                    exercise = "Manguito Rotador na Polia",
                    duration = null,
                    sets = 4,
                    repetitions = 12,
                    rest = null,
                    observation = "Alternar entre os ombros e utilizar cargas reduzidas"
                )
            )
        ),
        DayWeekWorkoutGroupDecorator(
            id = "2",
            label = "Peito",
            items = listOf(
                DayWeekWorkoutItemDecorator(
                    id = "1",
                    exercise = "Supino Inclinado com Halteres",
                    duration = null,
                    sets = 4,
                    repetitions = 12,
                    rest = 45,
                    observation = "Segurar embaixo por 2 segundos em toda repetição"
                ),
                DayWeekWorkoutItemDecorator(
                    id = "2",
                    exercise = "Crussifixo Reto com Halteres",
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