package br.com.fitnesspro.workout.ui.state

import androidx.paging.PagingData
import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.simplefilter.SimpleFilterState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.fitnesspro.tuple.ExecutionEvolutionHistoryGroupedTuple
import br.com.fitnesspro.workout.ui.state.reports.NewRegisterEvolutionReportDialogUIState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class ExecutionEvolutionHistoryUIState(
    val simpleFilterState: SimpleFilterState = SimpleFilterState(),
    val history: Flow<PagingData<ExecutionEvolutionHistoryGroupedTuple>> = emptyFlow(),
    val newRegisterEvolutionReportDialogUIState: NewRegisterEvolutionReportDialogUIState = NewRegisterEvolutionReportDialogUIState(),
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override var executeLoad: Boolean = true
): IThrowableUIState, ISuspendedLoadUIState