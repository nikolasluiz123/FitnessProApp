package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition

data class PreDefinitionGroupDialogUIState(
    val title: String = "",
    val name: TextField = TextField(),
    val toWorkoutGroupPreDefinition: TOWorkoutGroupPreDefinition = TOWorkoutGroupPreDefinition(),
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val showDialog: Boolean = false,
    val onShowDialogChange: (Boolean) -> Unit = { },
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { }
): ILoadingUIState