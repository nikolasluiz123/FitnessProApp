package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.core.state.IThrowableUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.workout.ui.screen.current.workout.decorator.CurrentWorkoutDecorator


data class CurrentWorkoutUIState(
    val subtitle: String? = null,
    val items: List<CurrentWorkoutDecorator> = mutableListOf(),
    val toWorkout: TOWorkout? = null,
    override val messageDialogState: MessageDialogState = MessageDialogState()
): IThrowableUIState