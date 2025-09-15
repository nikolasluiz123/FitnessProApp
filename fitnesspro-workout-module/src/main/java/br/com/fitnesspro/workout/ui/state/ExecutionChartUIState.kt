package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.charts.states.bar.GroupedBarChartState
import br.com.fitnesspro.charts.states.line.LineChartState
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.ISuspendedLoadUIState
import br.com.fitnesspro.core.state.IThrowableUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.tuple.charts.ExerciseExecutionChartTuple

data class ExecutionChartUIState(
    val title: String = "",
    val subtitle: String = "",
    val barChartState: GroupedBarChartState = GroupedBarChartState(),
    val lineChartState: LineChartState = LineChartState(),
    val chartData: List<ExerciseExecutionChartTuple> = emptyList(),
    val filterDialogState: ExecutionChartFiltersDialogUIState = ExecutionChartFiltersDialogUIState(),
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {},
    override var executeLoad: Boolean = true
): ILoadingUIState, IThrowableUIState, ISuspendedLoadUIState