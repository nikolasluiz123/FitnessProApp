package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.R.drawable
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_700

@Composable
fun IconButtonCalendar(
    modifier: Modifier = Modifier,
    iconColor: Color = GREY_700,
    enabled: Boolean = true,
    contentDescriptionResId: Int? = R.string.label_calendar,
    onClick: () -> Unit = { }
) {
    FitnessProIconButton(
        modifier = modifier,
        resId = drawable.ic_calendar_24dp,
        iconColor = iconColor,
        enabled = enabled,
        contentDescriptionResId = contentDescriptionResId,
        onClick = onClick
    )
}

@Preview
@Composable
private fun IconButtonCalendarPreview() {
    FitnessProTheme {
        Surface {
            IconButtonCalendar()
        }
    }
}