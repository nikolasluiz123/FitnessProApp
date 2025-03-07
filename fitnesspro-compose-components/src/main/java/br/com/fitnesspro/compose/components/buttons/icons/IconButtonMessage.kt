package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_800

@Composable
fun IconButtonMessage(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    iconColor: Color = GREY_800,
    enabled: Boolean = true,
    contentDescriptionResId: Int? = R.string.label_message,
    hasMessagesToRead: Boolean = false,
    onClick: () -> Unit = { }
) {
    val resId = if (hasMessagesToRead) {
        br.com.fitnesspro.core.R.drawable.ic_message_with_notification_32dp
    } else {
        br.com.fitnesspro.core.R.drawable.ic_message_32dp
    }

    FitnessProIconButton(
        modifier = modifier,
        iconModifier = iconModifier,
        resId = resId,
        iconColor = iconColor,
        enabled = enabled,
        contentDescriptionResId = contentDescriptionResId,
        onClick = onClick
    )
}



@Preview
@Composable
fun IconButtonMessagePreview() {
    FitnessProTheme {
        Surface {
            IconButtonMessage()
        }
    }
}