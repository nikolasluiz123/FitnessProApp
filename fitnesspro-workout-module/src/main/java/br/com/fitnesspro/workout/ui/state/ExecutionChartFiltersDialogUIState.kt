package br.com.fitnesspro.workout.ui.state

import br.com.android.ui.compose.components.checkbox.state.MultipleCheckBoxesState
import br.com.android.ui.compose.components.radiobutton.state.MultipleRadioButtonsState


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
