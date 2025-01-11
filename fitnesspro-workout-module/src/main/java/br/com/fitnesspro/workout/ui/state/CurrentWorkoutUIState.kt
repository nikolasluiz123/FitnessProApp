package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.workout.ui.screen.workout.decorator.CurrentWorkoutItemDecorator


data class CurrentWorkoutUIState(
    val title: String? = null,
    val subtitle: String? = null,
    val items: List<CurrentWorkoutItemDecorator> = mutableListOf()
)