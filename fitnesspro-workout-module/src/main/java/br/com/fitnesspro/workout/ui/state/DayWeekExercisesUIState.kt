package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.compose.components.filter.SimpleFilterState
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.decorator.DayWeekExercicesGroupDecorator

data class DayWeekExercisesUIState(
    var title: String = "",
    var subtitle: String = "",
    var isOverDue: Boolean = false,
    var deleteEnabled: Boolean = false,
    var groups: List<DayWeekExercicesGroupDecorator> = emptyList(),
    var filteredGroups: List<DayWeekExercicesGroupDecorator> = emptyList(),
    var messageDialogState: MessageDialogState = MessageDialogState(),
    val simpleFilterState: SimpleFilterState = SimpleFilterState(),
    var workoutGroupIdEdited: String? = null,
    val workout: TOWorkout? = null,
    val workoutGroupEditDialogUIState: WorkoutGroupEditDialogUIState = WorkoutGroupEditDialogUIState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { }
): ILoadingUIState
