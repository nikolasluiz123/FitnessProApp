package br.com.fitnesspro.workout.ui.state

import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.decorator.WorkoutGroupDecorator


data class DayWeekWorkoutUIState(
    val title: String = "",
    val subtitle: String= "",
    val dayWeekWorkoutGroups: List<WorkoutGroupDecorator> = emptyList(),
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override var executeLoad: Boolean = true,
): IThrowableUIState, ISuspendedLoadUIState