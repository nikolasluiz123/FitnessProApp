package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.compose.components.filter.SimpleFilterState
import br.com.fitnesspro.core.state.IThrowableUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOWorkout

data class MembersWorkoutUIState(
    val simpleFilterState: SimpleFilterState = SimpleFilterState(),
    val workouts: List<TOWorkout> = emptyList(),
    override val messageDialogState: MessageDialogState = MessageDialogState(),
): IThrowableUIState
