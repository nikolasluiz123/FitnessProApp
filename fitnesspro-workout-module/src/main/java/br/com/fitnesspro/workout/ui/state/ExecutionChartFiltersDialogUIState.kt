package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.compose.components.fields.state.checkbox.MultipleCheckBoxesState
import br.com.fitnesspro.compose.components.fields.state.radiobutton.MultipleRadioButtonsState

data class ExecutionChartFiltersDialogUIState(
    val focusValueRadioButtons: MultipleRadioButtonsState = MultipleRadioButtonsState(),
    val metricValueRadioButtons: MultipleRadioButtonsState = MultipleRadioButtonsState(),
    val chartTypeRadioButtons: MultipleRadioButtonsState = MultipleRadioButtonsState(),
    val showValuesCheckboxes: MultipleCheckBoxesState = MultipleCheckBoxesState(),
    val showDialog: Boolean = false,
    val onShowDialogChange: (Boolean) -> Unit = { },
    val onRestoreClick: () -> Unit = { },
    val onApplyClick: () -> Unit = { }
)
