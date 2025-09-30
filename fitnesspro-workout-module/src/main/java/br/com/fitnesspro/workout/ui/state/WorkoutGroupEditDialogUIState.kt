package br.com.fitnesspro.workout.ui.state

import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.fields.dropdown.state.DropDownTextField
import br.com.android.ui.compose.components.fields.text.state.TextField
import br.com.android.ui.compose.components.states.ILoadingUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.fitnesspro.to.TOWorkoutGroup
import java.time.DayOfWeek

data class WorkoutGroupEditDialogUIState(
    val title: String = "",
    val name: TextField = TextField(),
    val order: TextField = TextField(),
    val dayWeek: DropDownTextField<DayOfWeek> = DropDownTextField(),
    val toWorkoutGroup: TOWorkoutGroup = TOWorkoutGroup(),
    val showDialog: Boolean = false,
    val onShowDialogChange: (Boolean) -> Unit = { },
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { }
): ILoadingUIState, IThrowableUIState