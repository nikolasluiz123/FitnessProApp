package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.compose.components.filter.SimpleFilterState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOWorkout

data class MembersWorkoutUIState(
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val simpleFilterState: SimpleFilterState = SimpleFilterState(),
    val workouts: List<TOWorkout> = emptyList(),
)
