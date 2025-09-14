package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.compose.components.fields.state.radiobutton.MultipleRadioButtonsState

data class ExecutionGroupedBarChartFiltersDialogUIState(
    val focusValueRadioButtons: MultipleRadioButtonsState = MultipleRadioButtonsState(),
    val metricValueRadioButtons: MultipleRadioButtonsState = MultipleRadioButtonsState(),
    val showDialog: Boolean = false,
    val onShowDialogChange: (Boolean) -> Unit = { },
    val onRestoreClick: () -> Unit = { },
    val onApplyClick: () -> Unit = { }
)
