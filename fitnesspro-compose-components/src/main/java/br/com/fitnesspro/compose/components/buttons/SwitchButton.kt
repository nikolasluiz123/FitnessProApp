package br.com.fitnesspro.compose.components.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState.Off
import androidx.compose.ui.state.ToggleableState.On
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.buttons.enums.EnumSwitchButtonTestTags.HORIZONTAL_LABELED_SWITCH_BUTTON
import br.com.fitnesspro.compose.components.buttons.enums.EnumSwitchButtonTestTags.HORIZONTAL_LABELED_SWITCH_BUTTON_LABEL
import br.com.fitnesspro.compose.components.buttons.enums.EnumSwitchButtonTestTags.SWITCH_BUTTON
import br.com.fitnesspro.compose.components.buttons.enums.EnumSwitchButtonTestTags.SWITCH_BUTTON_CHECKED_ICON
import br.com.fitnesspro.compose.components.buttons.enums.EnumSwitchButtonTestTags.SWITCH_BUTTON_UNCHECKED_ICON
import br.com.fitnesspro.compose.components.fields.state.SwitchButtonField
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.LabelTextStyle

@Composable
fun FitnessProSwitchButton(
    field: SwitchButtonField,
    modifier: Modifier = Modifier
) {
    var checked by remember { mutableStateOf(field.checked) }

    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
            field.onCheckedChange(it)
        },
        enabled = field.enabled,
        colors = getFitnessProSwitchButtonColors(),
        thumbContent = { FitnessProSwitchButtonIcon(field) },
        modifier = modifier
            .clearAndSetSemantics {
                testTag = SWITCH_BUTTON.name
                role = Role.Switch
                toggleableState = if (checked) On else Off
            }
    )
}

@Composable
private fun getFitnessProSwitchButtonColors(): SwitchColors {
    return SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
        checkedTrackColor = MaterialTheme.colorScheme.primary,
        checkedBorderColor = Color.Transparent,
        checkedIconColor = MaterialTheme.colorScheme.primary,
        uncheckedThumbColor = MaterialTheme.colorScheme.primary,
        uncheckedTrackColor = MaterialTheme.colorScheme.onPrimary,
        uncheckedBorderColor = MaterialTheme.colorScheme.primary,
        uncheckedIconColor = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
private fun FitnessProSwitchButtonIcon(field: SwitchButtonField) {
    if (field.checked) {
        Icon(
            painter = painterResource(R.drawable.ic_switch_button_checked),
            contentDescription = null,
            modifier = Modifier
                .testTag(SWITCH_BUTTON_CHECKED_ICON.name)
                .size(SwitchDefaults.IconSize)
        )
    } else {
        Icon(
            painter = painterResource(R.drawable.ic_switch_button_unchecked),
            contentDescription = null,
            modifier = Modifier
                .testTag(SWITCH_BUTTON_UNCHECKED_ICON.name)
                .size(SwitchDefaults.IconSize)
        )
    }
}

@Composable
fun HorizontalLabeledSwitchButton(
    field: SwitchButtonField,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.testTag(HORIZONTAL_LABELED_SWITCH_BUTTON.name),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FitnessProSwitchButton(field)
        Text(
            modifier = Modifier.testTag(HORIZONTAL_LABELED_SWITCH_BUTTON_LABEL.name),
            text = label,
            style = LabelTextStyle,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun FitnessProSwitchButtonCheckedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProSwitchButton(
                field = SwitchButtonField(
                    checked = true,
                    onCheckedChange = { }
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun FitnessProSwitchButtonUncheckedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProSwitchButton(
                field = SwitchButtonField(
                    checked = false,
                    onCheckedChange = { }
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun HorizontalLabeledSwitchButtonCheckedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            HorizontalLabeledSwitchButton(
                field = SwitchButtonField(
                    checked = true,
                    onCheckedChange = { }
                ),
                label = "Label"
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun FitnessProSwitchButtonCheckedPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProSwitchButton(
                field = SwitchButtonField(
                    checked = true,
                    onCheckedChange = { }
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun FitnessProSwitchButtonUncheckedPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProSwitchButton(
                field = SwitchButtonField(
                    checked = false,
                    onCheckedChange = { }
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun HorizontalLabeledSwitchButtonCheckedPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            HorizontalLabeledSwitchButton(
                field = SwitchButtonField(
                    checked = true,
                    onCheckedChange = { }
                ),
                label = "Label"
            )
        }
    }
}