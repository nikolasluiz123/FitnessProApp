package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.decorator.DayWeekExercicesGroupDecorator

data class DayWeekExercisesUIState(
    var title: String? = null,
    var subtitle: String? = null,
    var isOverDue: Boolean = false,
    var deleteEnabled: Boolean = false,
    var groups: List<DayWeekExercicesGroupDecorator> = emptyList(),
    var messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { }
): ILoadingUIState
