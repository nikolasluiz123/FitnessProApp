package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.charts.states.bar.GroupedBarChartState
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.ISuspendedLoadUIState
import br.com.fitnesspro.core.state.IThrowableUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.tuple.charts.ExerciseExecutionGroupedBarChartTuple

data class ExecutionGroupedBarChartUIState(
    val title: String = "",
    val subtitle: String = "",
    val chartState: GroupedBarChartState = GroupedBarChartState(),
    val chartData: List<ExerciseExecutionGroupedBarChartTuple> = emptyList(),
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {},
    override var executeLoad: Boolean = true
): ILoadingUIState, IThrowableUIState, ISuspendedLoadUIState