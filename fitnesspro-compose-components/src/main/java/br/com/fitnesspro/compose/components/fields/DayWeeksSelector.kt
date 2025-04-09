package br.com.fitnesspro.compose.components.fields

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.fields.enums.EnumDayWeeksSelectorTestTags.DAY_WEEKS_SELECTOR_WEEK_CELL
import br.com.fitnesspro.compose.components.fields.state.DayWeeksSelectorField
import br.com.fitnesspro.core.extensions.getShortDisplayName
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.LabelTextStyle
import br.com.fitnesspro.core.theme.RED_400
import java.time.DayOfWeek

@Composable
fun DayWeeksSelector(
    selectorField: DayWeeksSelectorField,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            DayOfWeek.entries.forEach { week ->
                DayWeekCell(
                    week = week,
                    isSelected = selectorField.selected.contains(week),
                    onWeekClick = {
                        if (selectorField.selected.contains(it)) {
                            selectorField.selected.remove(it)
                        } else {
                            selectorField.selected.add(it)
                        }

                        selectorField.onSelect(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun DayWeekCell(
    week: DayOfWeek,
    isSelected: Boolean,
    onWeekClick: (DayOfWeek) -> Unit
) {
    val colorAnimationDuration = 300

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) RED_400 else Color.Transparent,
        animationSpec = tween(durationMillis = colorAnimationDuration),
        label = "dayWeekCellBackgroundColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(durationMillis = colorAnimationDuration),
        label = "dayWeekCellTextColor"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        Modifier
            .padding(4.dp)
            .size(40.dp)
            .background(color = backgroundColor, shape = CircleShape)
            .testTag(DAY_WEEKS_SELECTOR_WEEK_CELL.name)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, radius = 20.dp, color = Color.Gray)
            ) {
                onWeekClick(week)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = week.getShortDisplayName(),
            style = LabelTextStyle,
            color = textColor
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeeksSelectorPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayWeeksSelector(
                selectorField = DayWeeksSelectorField(
                    selected = mutableListOf(),
                    onSelect = { }
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeeksSelectorWithSelectedValuesPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayWeeksSelector(
                selectorField = DayWeeksSelectorField(
                    selected = mutableListOf(DayOfWeek.FRIDAY, DayOfWeek.WEDNESDAY),
                    onSelect = { }
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeeksSelectorPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            DayWeeksSelector(
                selectorField = DayWeeksSelectorField(
                    selected = mutableListOf(),
                    onSelect = { }
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeeksSelectorWithSelectedValuesPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            DayWeeksSelector(
                selectorField = DayWeeksSelectorField(
                    selected = mutableListOf(DayOfWeek.FRIDAY, DayOfWeek.WEDNESDAY),
                    onSelect = { }
                )
            )
        }
    }
}