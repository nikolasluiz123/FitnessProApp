package br.com.fitnesspro.ui.state

import br.com.fitnesspro.ui.screen.workout.decorator.CurrentWorkoutItemDecorator

data class CurrentWorkoutUIState(
    val title: String? = null,
    val subtitle: String? = null,
    val items: List<CurrentWorkoutItemDecorator> = mutableListOf()
)