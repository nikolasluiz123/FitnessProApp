package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.android.compose.charts.entries.bar.GroupedBarEntry
import br.com.android.compose.charts.entries.legend.LegendEntry
import br.com.android.compose.charts.entries.line.LineChartPointEntry
import br.com.android.compose.charts.states.legend.ChartLegendState
import br.com.android.ui.compose.components.checkbox.state.CheckBoxState
import br.com.android.ui.compose.components.checkbox.state.MultipleCheckBoxesState
import br.com.android.ui.compose.components.dialog.message.showErrorDialog
import br.com.android.ui.compose.components.radiobutton.state.MultipleRadioButtonsState
import br.com.android.ui.compose.components.radiobutton.state.RadioButtonState
import br.com.core.utils.enums.EnumDateTimePatterns
import br.com.core.utils.extensions.format
import br.com.core.utils.extensions.fromJsonNavParamToArgs
import br.com.core.utils.extensions.millisTo
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.tuple.charts.ExerciseExecutionChartTuple
import br.com.fitnesspro.workout.R.string
import br.com.fitnesspro.workout.repository.ExerciseExecutionRepository
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.WorkoutRepository
import br.com.fitnesspro.workout.ui.navigation.ExecutionChartScreenArgs
import br.com.fitnesspro.workout.ui.navigation.executionChartScreenArguments
import br.com.fitnesspro.workout.ui.screen.charts.enums.EnumChartType
import br.com.fitnesspro.workout.ui.screen.charts.enums.EnumFocusValue
import br.com.fitnesspro.workout.ui.screen.charts.enums.EnumMetricsValue
import br.com.fitnesspro.workout.ui.screen.charts.enums.EnumVisibleValues
import br.com.fitnesspro.workout.ui.state.ExecutionChartFiltersDialogUIState
import br.com.fitnesspro.workout.ui.state.ExecutionChartUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ExecutionChartViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val exerciseExecutionRepository: ExerciseExecutionRepository,
    private val personRepository: PersonRepository,
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository,
    savedStateHandle: SavedStateHandle
): FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<ExecutionChartUIState> = MutableStateFlow(ExecutionChartUIState())
    val uiState get() = _uiState.asStateFlow()

    val jsonArgs: String? = savedStateHandle[executionChartScreenArguments]

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
            },
            filterDialogState = ExecutionChartFiltersDialogUIState(
                focusValueRadioButtons = MultipleRadioButtonsState(
                    radioButtons = getDefaultListFocusValueRadioButtons(),
                    onRadioButtonClick = ::onFocusValueRadioButtonClick
                ),
                metricValueRadioButtons = MultipleRadioButtonsState(
                    radioButtons = getDefaultListMetricValueRadioButtons(),
                    onRadioButtonClick = ::onMetricValueRadioButtonClick
                ),
                chartTypeRadioButtons = MultipleRadioButtonsState(
                    radioButtons = getDefaultListChartTypeRadioButtons(),
                    onRadioButtonClick = ::onChartTypeRadioButtonClick
                ),
                showValuesCheckboxes = MultipleCheckBoxesState(
                    onCheckBoxClick = {
                        _uiState.value = _uiState.value.copy(
                            filterDialogState = _uiState.value.filterDialogState.copy(
                                showValuesCheckboxes = _uiState.value.filterDialogState.showValuesCheckboxes.copy(
                                    checkBoxes = updateCheckBoxesListOnClick(it)
                                )
                            )
                        )
                    }
                ),
                onRestoreClick = {
                    _uiState.value = _uiState.value.copy(
                        filterDialogState = _uiState.value.filterDialogState.copy(
                            focusValueRadioButtons = _uiState.value.filterDialogState.focusValueRadioButtons.copy(
                                radioButtons = getDefaultListFocusValueRadioButtons()
                            ),
                            metricValueRadioButtons = _uiState.value.filterDialogState.metricValueRadioButtons.copy(
                                radioButtons = getDefaultListMetricValueRadioButtons()
                            ),
                            chartTypeRadioButtons = _uiState.value.filterDialogState.chartTypeRadioButtons.copy(
                                radioButtons = getDefaultListChartTypeRadioButtons()
                            )
                        ),
                        chartType = EnumChartType.GROUPED_BAR
                    )

                    _uiState.value = _uiState.value.copy(
                        barChartState = _uiState.value.barChartState.copy(
                            entries = getGroupedBarChartEntries(_uiState.value.chartData),
                        )
                    )
                },
                onApplyClick = {
                    launch {
                        loadChartStates(_uiState.value.chartData)

                        _uiState.value = _uiState.value.copy(
                            chartType = getChartTypeOption().identifier as EnumChartType
                        )
                    }
                },
                onShowDialogChange = {
                    _uiState.value = _uiState.value.copy(
                        filterDialogState = _uiState.value.filterDialogState.copy(showDialog = it)
                    )
                }
            )
        )
    }

    private fun updateCheckBoxesListOnClick(clickedCheckbox: Enum<*>): List<CheckBoxState> {
        clickedCheckbox as EnumVisibleValues

        return _uiState.value.filterDialogState.showValuesCheckboxes.checkBoxes.map { checkBoxState ->
            if (checkBoxState.identifier == clickedCheckbox) {
                val toggledValue = if (checkBoxState.enabled) !checkBoxState.checked else checkBoxState.checked
                checkBoxState.copy(checked = toggledValue)
            } else {
                checkBoxState
            }
        }
    }

    private fun getDefaultListCheckboxesShowValues(chartData: List<ExerciseExecutionChartTuple>): List<CheckBoxState> {
        val hasWeightData = chartData.any { it.weight != null }
        val hasRepsData = chartData.any { it.reps != null }
        val hasRestData = chartData.any { it.rest != null }
        val hasDurationData = chartData.any { it.duration != null }

        return listOf(
            CheckBoxState(
                label = context.getString(string.excution_grouped_bar_chart_filters_screen_label_weight),
                identifier = EnumVisibleValues.WEIGHT,
                enabled = hasWeightData,
                checked = hasWeightData
            ),
            CheckBoxState(
                label = context.getString(string.excution_grouped_bar_chart_filters_screen_label_reps),
                identifier = EnumVisibleValues.REPS,
                enabled = hasRepsData,
                checked = hasRepsData
            ),
            CheckBoxState(
                label = context.getString(string.excution_grouped_bar_chart_filters_screen_label_rest),
                identifier = EnumVisibleValues.REST,
                enabled = hasRestData,
                checked = hasRestData
            ),
            CheckBoxState(
                label = context.getString(string.excution_grouped_bar_chart_filters_screen_label_duration),
                identifier = EnumVisibleValues.DURATION,
                enabled = hasDurationData,
                checked = hasDurationData
            )
        )
    }

    private fun getChartTypeOption(): RadioButtonState {
        return _uiState.value.filterDialogState.chartTypeRadioButtons.radioButtons.first { it.selected }
    }

    private fun getDefaultListFocusValueRadioButtons(): List<RadioButtonState> {
        return listOf(
            RadioButtonState(
                label = context.getString(string.excution_grouped_bar_chart_filters_screen_label_weight),
                identifier = EnumFocusValue.WEIGHT,
                selected = true
            ),
            RadioButtonState(
                label = context.getString(string.excution_grouped_bar_chart_filters_screen_label_reps),
                identifier = EnumFocusValue.REPS
            ),
            RadioButtonState(
                label = context.getString(string.excution_grouped_bar_chart_filters_screen_label_rest),
                identifier = EnumFocusValue.REST
            ),
            RadioButtonState(
                label = context.getString(string.excution_grouped_bar_chart_filters_screen_label_duration),
                identifier = EnumFocusValue.DURATION
            )
        )
    }

    private fun getDefaultListMetricValueRadioButtons(): List<RadioButtonState> {
        return listOf(
            RadioButtonState(
                label = context.getString(string.excution_grouped_bar_chart_filters_screen_label_min),
                identifier = EnumMetricsValue.MIN
            ),
            RadioButtonState(
                label = context.getString(string.excution_grouped_bar_chart_filters_screen_label_max),
                identifier = EnumMetricsValue.MAX,
                selected = true
            ),
            RadioButtonState(
                label = context.getString(string.excution_grouped_bar_chart_filters_screen_label_avg),
                identifier = EnumMetricsValue.AVG
            )
        )
    }

    private fun getDefaultListChartTypeRadioButtons(): List<RadioButtonState> {
        return listOf(
            RadioButtonState(
                label = context.getString(string.excution_grouped_bar_chart_filters_screen_label_grouped_bar),
                identifier = EnumChartType.GROUPED_BAR,
                selected = true
            ),
            RadioButtonState(
                label = context.getString(string.excution_grouped_bar_chart_filters_screen_label_lines),
                identifier = EnumChartType.LINES,
            )
        )
    }

    private fun onChartTypeRadioButtonClick(identifier: Enum<*>) {
        val newButtonsList = getChartTypeRadioButtonsListWithSelection(identifier)

        _uiState.value = _uiState.value.copy(
            filterDialogState = _uiState.value.filterDialogState.copy(
                chartTypeRadioButtons = _uiState.value.filterDialogState.chartTypeRadioButtons.copy(
                    radioButtons = newButtonsList
                )
            )
        )
    }

    private fun getChartTypeRadioButtonsListWithSelection(identifier: Enum<*>): List<RadioButtonState> {
        val currentButtons = _uiState.value.filterDialogState.chartTypeRadioButtons.radioButtons

        return currentButtons.map { radioButtonState ->
            radioButtonState.copy(selected = radioButtonState.identifier == identifier)
        }
    }

    private fun onFocusValueRadioButtonClick(identifier: Enum<*>) {
        val newButtonsList = getFocusValueRadioButtonsListWithSelection(identifier)

        _uiState.value = _uiState.value.copy(
            filterDialogState = _uiState.value.filterDialogState.copy(
                focusValueRadioButtons = _uiState.value.filterDialogState.focusValueRadioButtons.copy(
                    radioButtons = newButtonsList
                )
            )
        )
    }

    private fun getFocusValueRadioButtonsListWithSelection(identifier: Enum<*>): List<RadioButtonState> {
        val currentButtons = _uiState.value.filterDialogState.focusValueRadioButtons.radioButtons

        return currentButtons.map { radioButtonState ->
            radioButtonState.copy(selected = radioButtonState.identifier == identifier)
        }
    }

    private fun onMetricValueRadioButtonClick(identifier: Enum<*>) {
        val newMetricButtonsList = getMetricValueRadioButtonsListWithSelection(identifier)

        val newFocusValueButtonsList = getUpdatedFocusValueRadioButtonListWithMetric(identifier)

        _uiState.value = _uiState.value.copy(
            filterDialogState = _uiState.value.filterDialogState.copy(
                metricValueRadioButtons = _uiState.value.filterDialogState.metricValueRadioButtons.copy(
                    radioButtons = newMetricButtonsList
                ),
                focusValueRadioButtons = _uiState.value.filterDialogState.focusValueRadioButtons.copy(
                    radioButtons = newFocusValueButtonsList
                )
            )
        )
    }

    private fun getUpdatedFocusValueRadioButtonListWithMetric(identifier: Enum<*>): List<RadioButtonState> {
        val currentFocusValueButtons = _uiState.value.filterDialogState.focusValueRadioButtons.radioButtons

        val focusableMetrics = listOf(EnumMetricsValue.MIN, EnumMetricsValue.MAX)

        return if (identifier in focusableMetrics) {
            currentFocusValueButtons.map { radioButtonState ->
                radioButtonState.copy(enabled = true, selected = radioButtonState.identifier == EnumFocusValue.WEIGHT)
            }
        } else {
            currentFocusValueButtons.map { radioButtonState ->
                radioButtonState.copy(enabled = false, selected = false)
            }
        }
    }

    private fun getMetricValueRadioButtonsListWithSelection(identifier: Enum<*>): List<RadioButtonState> {
        val currentMetricButtons = _uiState.value.filterDialogState.metricValueRadioButtons.radioButtons

        return currentMetricButtons.map { radioButtonState ->
            radioButtonState.copy(selected = radioButtonState.identifier == identifier)
        }
    }

    fun loadStateWithDatabaseInfos() {
        val args = jsonArgs?.fromJsonNavParamToArgs(ExecutionChartScreenArgs::class.java)!!

        launch {
            loadTopAppBarData(args)
            loadChartData(args)

            _uiState.value = _uiState.value.copy(executeLoad = false)
        }
    }

    private suspend fun loadTopAppBarData(args: ExecutionChartScreenArgs) {
        val workout = workoutRepository.findWorkoutByExerciseId(args.exerciseId)

        _uiState.value = _uiState.value.copy(
            title = getTitle(workout),
            subtitle = getSubtitle(args, workout)
        )
    }

    private suspend fun getTitle(workout: TOWorkout?): String {
        val member = personRepository.getTOPersonById(workout?.academyMemberPersonId!!)
        val authenticatedUserType = personRepository.getAuthenticatedTOPerson()?.user?.type!!

        return if (authenticatedUserType == EnumUserType.ACADEMY_MEMBER) {
            context.getString(string.execution_grouped_bar_chart_title_member)
        } else {
            context.getString(
                string.execution_grouped_bar_chart_title_professional,
                member.name!!
            )
        }
    }

    private suspend fun getSubtitle(args: ExecutionChartScreenArgs, workout: TOWorkout?): String {
        val exerciseName = exerciseRepository.findById(args.exerciseId).name!!

        val dateRange = context.getString(
            string.execution_grouped_bar_chart_title_workout_date_range,
            workout?.dateStart!!.format(EnumDateTimePatterns.DAY_MONTH_DATE),
            workout.dateEnd!!.format(EnumDateTimePatterns.DAY_MONTH_DATE)
        )

        return "$exerciseName - $dateRange"
    }

    private suspend fun loadChartData(args: ExecutionChartScreenArgs) {
        val chartData = exerciseExecutionRepository.getListExerciseExecutionGroupedBarChartTuple(args.exerciseId)

        _uiState.value = _uiState.value.copy(
            chartData = chartData,
            filterDialogState = _uiState.value.filterDialogState.copy(
                showValuesCheckboxes = _uiState.value.filterDialogState.showValuesCheckboxes.copy(
                    checkBoxes = getDefaultListCheckboxesShowValues(chartData),
                )
            )
        )

        loadChartStates(chartData)
    }

    private fun loadChartStates(chartData: List<ExerciseExecutionChartTuple>) {
        if (getChartTypeOption().identifier == EnumChartType.GROUPED_BAR) {
            _uiState.value = _uiState.value.copy(
                barChartState = _uiState.value.barChartState.copy(
                    entries = getGroupedBarChartEntries(chartData),
                    legendState = getLegendState(chartData)
                )
            )
        } else {
            _uiState.value = _uiState.value.copy(
                lineChartState = _uiState.value.lineChartState.copy(
                    entries = getLineChartEntries(chartData),
                    legendState = getLegendState(chartData)
                )
            )
        }
    }

    private fun getGroupedBarChartEntries(chartData: List<ExerciseExecutionChartTuple>): List<GroupedBarEntry> {
        val groupedData = chartData.groupBy { it.date }

        return groupedData.map { mapEntry ->
            val formatedDate = mapEntry.key.format(EnumDateTimePatterns.DAY_MONTH_DATE)
            val values = getFilteredValues(mapEntry)

            GroupedBarEntry(label = formatedDate, values = values)
        }
    }

    private fun getLineChartEntries(chartData: List<ExerciseExecutionChartTuple>): List<LineChartPointEntry> {
        val groupedData = chartData.groupBy { it.date }

        return groupedData.map { mapEntry ->
            val formatedDate = mapEntry.key.format(EnumDateTimePatterns.DAY_MONTH_DATE)
            val values = getFilteredValues(mapEntry)

            LineChartPointEntry(label = formatedDate, values = values)
        }
    }

    private fun getFilteredValues(mapEntry: Map.Entry<LocalDate, List<ExerciseExecutionChartTuple>>): List<Float> {
        val metricSelected = getMetricSelected()

        return when (metricSelected) {
            EnumMetricsValue.MIN -> {
                val focusSelected = getFocusSelected()

                when (focusSelected) {
                    EnumFocusValue.WEIGHT -> {
                        val objectWithMinWeight = mapEntry.value.minBy { it.weight ?: 0.0 }
                        getListValuesNotNull(objectWithMinWeight)
                    }

                    EnumFocusValue.REPS -> {
                        val objectWithMinReps = mapEntry.value.minBy { it.reps ?: 0 }
                        getListValuesNotNull(objectWithMinReps)
                    }

                    EnumFocusValue.REST -> {
                        val objectWithMinRest = mapEntry.value.minBy { it.rest ?: 0L }
                        getListValuesNotNull(objectWithMinRest)
                    }

                    EnumFocusValue.DURATION -> {
                        val objectWithMinDuration = mapEntry.value.minBy { it.duration ?: 0L }
                        getListValuesNotNull(objectWithMinDuration)
                    }
                }
            }

            EnumMetricsValue.MAX -> {
                val focusSelected = getFocusSelected()

                when (focusSelected) {
                    EnumFocusValue.WEIGHT -> {
                        val objectWithMinWeight = mapEntry.value.maxBy { it.weight ?: 0.0 }
                        getListValuesNotNull(objectWithMinWeight)
                    }

                    EnumFocusValue.REPS -> {
                        val objectWithMinReps = mapEntry.value.maxBy { it.reps ?: 0 }
                        getListValuesNotNull(objectWithMinReps)
                    }

                    EnumFocusValue.REST -> {
                        val objectWithMinRest = mapEntry.value.maxBy { it.rest ?: 0L }
                        getListValuesNotNull(objectWithMinRest)
                    }

                    EnumFocusValue.DURATION -> {
                        val objectWithMinDuration = mapEntry.value.maxBy { it.duration ?: 0L }
                        getListValuesNotNull(objectWithMinDuration)
                    }
                }
            }

            EnumMetricsValue.AVG -> {
                val result = mutableListOf<Float>()
                val visibleValues = getVisibleValues()

                visibleValues.forEach { visibleValue ->
                    when (visibleValue) {
                        EnumVisibleValues.WEIGHT -> {
                            val weightAverage = mapEntry.value.map { it.weight ?: 0.0 }.average().toFloat()
                            if (weightAverage > 0) result.add(weightAverage)
                        }
                        EnumVisibleValues.REPS -> {
                            val repsAverage = mapEntry.value.map { it.reps ?: 0 }.average().toFloat()
                            if (repsAverage > 0) result.add(repsAverage)
                        }
                        EnumVisibleValues.REST -> {
                            val restAverage = mapEntry.value.map { it.rest?.millisTo(ChronoUnit.SECONDS) ?: 0L }.average().toFloat()
                            if (restAverage > 0) result.add(restAverage)
                        }
                        EnumVisibleValues.DURATION -> {
                            val durationAverage = mapEntry.value.map { it.duration?.millisTo(ChronoUnit.SECONDS) ?: 0L }.average().toFloat()
                            if (durationAverage > 0) result.add(durationAverage)
                        }
                    }
                }

                result
            }
        }
    }

    private fun getListValuesNotNull(objectWithMinWeight: ExerciseExecutionChartTuple): List<Float> {
        val result = mutableListOf<Float?>()

        val visibleValues = getVisibleValues()

        visibleValues.forEach {
            when (it) {
                EnumVisibleValues.WEIGHT -> result.add(objectWithMinWeight.weight?.toFloat())
                EnumVisibleValues.REPS -> result.add(objectWithMinWeight.reps?.toFloat())
                EnumVisibleValues.REST -> result.add(objectWithMinWeight.rest?.millisTo(ChronoUnit.SECONDS)?.toFloat())
                EnumVisibleValues.DURATION -> result.add(objectWithMinWeight.duration?.millisTo(ChronoUnit.SECONDS)?.toFloat())
            }
        }

        return result.filterNotNull()
    }

    private fun getVisibleValues(): List<EnumVisibleValues> {
        return _uiState.value.filterDialogState.showValuesCheckboxes.checkBoxes
            .filter { it.checked }
            .map { it.identifier as EnumVisibleValues }
    }

    private fun getFocusSelected(): EnumFocusValue {
        return _uiState.value.filterDialogState.focusValueRadioButtons.radioButtons.first { it.selected }.identifier as EnumFocusValue
    }

    private fun getMetricSelected(): EnumMetricsValue {
        return _uiState.value.filterDialogState.metricValueRadioButtons.radioButtons.first { it.selected }.identifier as EnumMetricsValue
    }

    private fun getLegendState(chartData: List<ExerciseExecutionChartTuple>): ChartLegendState {
        val entries = mutableListOf<LegendEntry>()
        val visibleValues = getVisibleValues()

        if (chartData.any { it.weight != null } && EnumVisibleValues.WEIGHT in visibleValues) {
            entries.add(LegendEntry(label = context.getString(string.execution_grouped_bar_chart_legend_label_weight)))
        }

        if (chartData.any { it.reps != null } && EnumVisibleValues.REPS in visibleValues) {
            entries.add(LegendEntry(label = context.getString(string.execution_grouped_bar_chart_legend_label_reps)))
        }

        if (chartData.any { it.rest != null } && EnumVisibleValues.REST in visibleValues) {
            entries.add(LegendEntry(label = context.getString(string.execution_grouped_bar_chart_legend_label_rest)))
        }

        if (chartData.any { it.duration != null } && EnumVisibleValues.DURATION in visibleValues) {
            entries.add(LegendEntry(label = context.getString(string.execution_grouped_bar_chart_legend_label_duration)))
        }

        return ChartLegendState(entries = entries, isEnabled = entries.isNotEmpty())
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