package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.IThrowableUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition

data class PreDefinitionGroupDialogUIState(
    val title: String = "",
    val name: TextField = TextField(),
    val toWorkoutGroupPreDefinition: TOWorkoutGroupPreDefinition = TOWorkoutGroupPreDefinition(),
    val showDialog: Boolean = false,
    val onShowDialogChange: (Boolean) -> Unit = { },
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { }
): ILoadingUIState, IThrowableUIState