package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.workout.ui.screen.workout.decorator.DayWeekWorkoutGroupDecorator


data class DayWeekWorkoutUIState(
    val title: String,
    val subtitle: String,
    val dayWeekWorkoutGroups: List<DayWeekWorkoutGroupDecorator>,
    val messageDialogState: MessageDialogState = MessageDialogState(),
)