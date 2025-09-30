package br.com.fitnesspro.workout.ui.state

import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.simplefilter.SimpleFilterState
import br.com.android.ui.compose.components.states.ILoadingUIState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.decorator.DayWeekExercicesGroupDecorator

data class DayWeekExercisesUIState(
    var title: String = "",
    var subtitle: String = "",
    var isOverDue: Boolean = false,
    var deleteEnabled: Boolean = false,
    var groups: List<DayWeekExercicesGroupDecorator> = emptyList(),
    var filteredGroups: List<DayWeekExercicesGroupDecorator> = emptyList(),
    val simpleFilterState: SimpleFilterState = SimpleFilterState(),
    var workoutGroupIdEdited: String? = null,
    val workout: TOWorkout? = null,
    val workoutGroupEditDialogUIState: WorkoutGroupEditDialogUIState = WorkoutGroupEditDialogUIState(),
    override var messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { },
    override var executeLoad: Boolean = true
): ILoadingUIState, IThrowableUIState, ISuspendedLoadUIState
