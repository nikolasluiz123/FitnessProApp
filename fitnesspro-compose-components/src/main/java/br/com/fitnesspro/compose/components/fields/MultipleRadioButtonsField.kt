package br.com.fitnesspro.compose.components.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.fitnesspro.compose.components.buttons.LabeledRadioButton
import br.com.fitnesspro.compose.components.fields.state.radiobutton.MultipleRadioButtonsState
import br.com.fitnesspro.compose.components.layout.ResponsiveGridFlowLayout

@Composable
fun MultipleRadioButtonsField(
    state: MultipleRadioButtonsState,
    modifier: Modifier = Modifier
) {
    ResponsiveGridFlowLayout(
        maxColumns = state.maxColumns,
        modifier = modifier,
        content = {
            state.radioButtons.forEach { radioButtonState ->
                LabeledRadioButton(
                    state = radioButtonState,
                    onClick = {
                        state.onRadioButtonClick(radioButtonState.identifier)
                    }
                )
            }
        }
    )
}