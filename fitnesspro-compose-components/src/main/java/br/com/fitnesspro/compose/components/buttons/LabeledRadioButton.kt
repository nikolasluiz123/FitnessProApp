package br.com.fitnesspro.compose.components.buttons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.fitnesspro.compose.components.fields.state.radiobutton.RadioButtonState
import br.com.fitnesspro.core.theme.LabelTextStyle

@Composable
fun LabeledRadioButton(
    state: RadioButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(enabled = state.enabled) {
                onClick()
            }
    ) {
        RadioButton(selected = state.selected, enabled = state.enabled, onClick = onClick)
        Text(text = state.label, style = LabelTextStyle)
    }
}