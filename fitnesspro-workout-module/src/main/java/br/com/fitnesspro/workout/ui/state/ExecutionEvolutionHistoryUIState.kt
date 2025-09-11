package br.com.fitnesspro.workout.ui.state

import androidx.paging.PagingData
import br.com.fitnesspro.compose.components.filter.SimpleFilterState
import br.com.fitnesspro.core.state.ISuspendedLoadUIState
import br.com.fitnesspro.core.state.IThrowableUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.tuple.ExecutionEvolutionHistoryGroupedTuple
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class ExecutionEvolutionHistoryUIState(
    val simpleFilterState: SimpleFilterState = SimpleFilterState(),
    val history: Flow<PagingData<ExecutionEvolutionHistoryGroupedTuple>> = emptyFlow(),
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override var executeLoad: Boolean = true
): IThrowableUIState, ISuspendedLoadUIState