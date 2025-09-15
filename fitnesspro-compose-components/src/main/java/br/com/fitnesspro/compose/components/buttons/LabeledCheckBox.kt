package br.com.fitnesspro.compose.components.buttons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.fitnesspro.compose.components.fields.state.checkbox.CheckBoxState
import br.com.fitnesspro.core.theme.LabelTextStyle

@Composable
fun LabeledCheckBox(
    state: CheckBoxState,
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
        Checkbox(
            checked = state.checked,
            onCheckedChange = { onClick() },
            enabled = state.enabled,
        )
        Text(text = state.label, style = LabelTextStyle)
    }
}