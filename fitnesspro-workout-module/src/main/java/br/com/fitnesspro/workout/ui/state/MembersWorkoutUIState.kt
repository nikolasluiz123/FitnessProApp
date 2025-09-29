package br.com.fitnesspro.workout.ui.state

import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.simplefilter.SimpleFilterState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.fitnesspro.to.TOWorkout

data class MembersWorkoutUIState(
    val simpleFilterState: SimpleFilterState = SimpleFilterState(),
    val workouts: List<TOWorkout> = emptyList(),
    override val messageDialogState: MessageDialogState = MessageDialogState(),
): IThrowableUIState
