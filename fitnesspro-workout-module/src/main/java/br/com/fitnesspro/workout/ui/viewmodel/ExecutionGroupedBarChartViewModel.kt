package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.charts.entries.bar.GroupedBarEntry
import br.com.fitnesspro.charts.entries.legend.LegendEntry
import br.com.fitnesspro.charts.states.legend.ChartLegendState
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.millisTo
import br.com.fitnesspro.tuple.charts.ExerciseExecutionGroupedBarChartTuple
import br.com.fitnesspro.workout.repository.ExerciseExecutionRepository
import br.com.fitnesspro.workout.ui.navigation.ExecutionGroupedBarChartScreenArgs
import br.com.fitnesspro.workout.ui.navigation.executionGroupedBarChartScreenArguments
import br.com.fitnesspro.workout.ui.state.ExecutionGroupedBarChartUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ExecutionGroupedBarChartViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val exerciseExecutionRepository: ExerciseExecutionRepository,
    savedStateHandle: SavedStateHandle
): FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<ExecutionGroupedBarChartUIState> = MutableStateFlow(ExecutionGroupedBarChartUIState())
    val uiState get() = _uiState.asStateFlow()

    val jsonArgs: String? = savedStateHandle[executionGroupedBarChartScreenArguments]

    init {
        initialLoadUIState()
    }

    override fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { _uiState.value = _uiState.value.copy(messageDialogState = it) }
            ),
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(showLoading = _uiState.value.showLoading.not())
            }
        )
    }

    fun loadStateWithDatabaseInfos() {
        val args = jsonArgs?.fromJsonNavParamToArgs(ExecutionGroupedBarChartScreenArgs::class.java)!!

        launch {
            val chartData = exerciseExecutionRepository.getListExerciseExecutionGroupedBarChartTuple(args.exerciseId)

            _uiState.value = _uiState.value.copy(
                chartData = chartData,
                chartState = _uiState.value.chartState.copy(
                    entries = getChartEntries(chartData),
                    legendState = getLegendState()
                ),
                executeLoad = false,
            )
        }
    }

    private fun getChartEntries(chartData: List<ExerciseExecutionGroupedBarChartTuple>): List<GroupedBarEntry> {
        val groupedData = chartData.groupBy { it.date }

        return groupedData.map { mapEntry ->
            val formatedDate = mapEntry.key.format(EnumDateTimePatterns.DAY_MONTH_DATE)
            val values = listOf(
                mapEntry.value.maxOf { it.weight?.toFloat() ?: 0f },
                mapEntry.value.maxOf { it.reps?.toFloat() ?: 0f },
                mapEntry.value.maxOf { it.rest ?: 0L }.millisTo(ChronoUnit.SECONDS).toFloat(),
                mapEntry.value.maxOf { it.duration ?: 0L }.millisTo(ChronoUnit.SECONDS).toFloat()
            )


            GroupedBarEntry(label = formatedDate, values = values)
        }
    }

    private fun getLegendState(): ChartLegendState {
        return ChartLegendState(
            entries = listOf(
                LegendEntry(label = context.getString(br.com.fitnesspro.workout.R.string.execution_grouped_bar_chart_legend_label_weight)),
                LegendEntry(label = context.getString(br.com.fitnesspro.workout.R.string.execution_grouped_bar_chart_legend_label_reps)),
                LegendEntry(label = context.getString(br.com.fitnesspro.workout.R.string.execution_grouped_bar_chart_legend_label_rest)),
                LegendEntry(label = context.getString(br.com.fitnesspro.workout.R.string.execution_grouped_bar_chart_legend_label_duration))
            )
        )
    }

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun getGlobalEventsBus(): GlobalEvents {
        return globalEvents
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)

        if (_uiState.value.showLoading) {
            _uiState.value.onToggleLoading()
        }
    }
}