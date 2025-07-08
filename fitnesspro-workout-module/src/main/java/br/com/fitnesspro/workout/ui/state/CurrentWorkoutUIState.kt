package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.workout.ui.screen.current.workout.decorator.CurrentWorkoutDecorator


data class CurrentWorkoutUIState(
    val subtitle: String? = null,
    val items: List<CurrentWorkoutDecorator> = mutableListOf(),
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val toWorkout: TOWorkout? = null
)