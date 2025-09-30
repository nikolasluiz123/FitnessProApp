package br.com.fitnesspro.workout.ui.state

import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.workout.ui.screen.current.workout.decorator.CurrentWorkoutDecorator


data class CurrentWorkoutUIState(
    val subtitle: String? = null,
    val items: List<CurrentWorkoutDecorator> = mutableListOf(),
    val toWorkout: TOWorkout? = null,
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override var executeLoad: Boolean = true
): IThrowableUIState, ISuspendedLoadUIState