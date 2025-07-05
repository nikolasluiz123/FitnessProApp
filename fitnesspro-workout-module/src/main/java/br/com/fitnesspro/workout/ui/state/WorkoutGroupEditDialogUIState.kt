package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOWorkoutGroup
import java.time.DayOfWeek

data class WorkoutGroupEditDialogUIState(
    val title: String = "",
    val name: TextField = TextField(),
    val order: TextField = TextField(),
    val dayWeek: DropDownTextField<DayOfWeek> = DropDownTextField(),
    val toWorkoutGroup: TOWorkoutGroup = TOWorkoutGroup(),
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val showDialog: Boolean = false,
    val onShowDialogChange: (Boolean) -> Unit = { },
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { }
): ILoadingUIState