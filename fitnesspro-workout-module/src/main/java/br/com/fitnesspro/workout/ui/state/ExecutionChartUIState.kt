package br.com.fitnesspro.workout.ui.state

import br.com.android.compose.charts.states.bar.GroupedBarChartState
import br.com.android.compose.charts.states.line.LineChartState
import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.states.ILoadingUIState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.fitnesspro.tuple.charts.ExerciseExecutionChartTuple
import br.com.fitnesspro.workout.ui.screen.charts.enums.EnumChartType

data class ExecutionChartUIState(
    val title: String = "",
    val subtitle: String = "",
    val barChartState: GroupedBarChartState = GroupedBarChartState(),
    val lineChartState: LineChartState = LineChartState(),
    val chartData: List<ExerciseExecutionChartTuple> = emptyList(),
    val filterDialogState: ExecutionChartFiltersDialogUIState = ExecutionChartFiltersDialogUIState(),
    val chartType: EnumChartType = EnumChartType.GROUPED_BAR,
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {},
    override var executeLoad: Boolean = true
): ILoadingUIState, IThrowableUIState, ISuspendedLoadUIState