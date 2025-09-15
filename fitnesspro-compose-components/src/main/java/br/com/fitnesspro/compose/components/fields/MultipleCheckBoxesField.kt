package br.com.fitnesspro.compose.components.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.fitnesspro.compose.components.buttons.LabeledCheckBox
import br.com.fitnesspro.compose.components.fields.state.checkbox.MultipleCheckBoxesState
import br.com.fitnesspro.compose.components.layout.ResponsiveGridFlowLayout

@Composable
fun MultipleCheckBoxesField(
    state: MultipleCheckBoxesState,
    modifier: Modifier = Modifier
) {
    ResponsiveGridFlowLayout(
        maxColumns = state.maxColumns,
        modifier = modifier,
        content = {
            state.checkBoxes.forEach { checkBoxState ->
                LabeledCheckBox(
                    state = checkBoxState,
                    onClick = {
                        state.onCheckBoxClick(checkBoxState.identifier)
                    }
                )
            }
        }
    )
}