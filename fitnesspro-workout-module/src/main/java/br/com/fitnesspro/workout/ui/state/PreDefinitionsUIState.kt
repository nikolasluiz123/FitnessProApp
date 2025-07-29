package br.com.fitnesspro.workout.ui.state

import androidx.paging.PagingData
import br.com.fitnesspro.compose.components.filter.SimpleFilterState
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.tuple.ExercisePredefinitionGroupedTuple
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class PreDefinitionsUIState(
    val onToggleBottomSheetNewPredefinition: () -> Unit = {},
    val showBottomSheetNewPredefinition: Boolean = false,
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val simpleFilterState: SimpleFilterState = SimpleFilterState(),
    val predefinitions: Flow<PagingData<ExercisePredefinitionGroupedTuple>> = emptyFlow(),
    var workoutGroupPreDefinitionIdEdited: String? = null,
    val workoutGroupPreDefinitionGroupDialogUIState: PreDefinitionGroupDialogUIState = PreDefinitionGroupDialogUIState(),
    val authenticatedPersonId: String? = null,
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {}
): ILoadingUIState