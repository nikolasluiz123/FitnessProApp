package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.decorator.WorkoutGroupDecorator


data class DayWeekWorkoutUIState(
    val title: String,
    val subtitle: String,
    val dayWeekWorkoutGroups: List<WorkoutGroupDecorator>,
    val showFabAddExercise: Boolean = false,
    val messageDialogState: MessageDialogState = MessageDialogState(),
)