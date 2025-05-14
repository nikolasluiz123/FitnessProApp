package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.workout.ui.screen.membersworkout.decorator.MemberWorkoutDecorator

data class MembersWorkoutUIState(
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val members: List<MemberWorkoutDecorator> = emptyList()
)
