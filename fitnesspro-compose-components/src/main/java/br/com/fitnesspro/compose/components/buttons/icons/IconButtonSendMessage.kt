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
fun IconButtonSendMessage(
    modifier: Modifier = Modifier,
    iconColor: Color = GREY_800,
    enabled: Boolean = true,
    contentDescriptionResId: Int? = R.string.label_send_message,
    onClick: () -> Unit = { }
) {
    FitnessProIconButton(
        modifier = modifier,
        resId = br.com.fitnesspro.core.R.drawable.ic_btn_send_message_32dp,
        iconColor = iconColor,
        enabled = enabled,
        contentDescriptionResId = contentDescriptionResId,
        onClick = onClick
    )
}


@Preview
@Composable
fun IconButtonSendMessagePreview() {
    FitnessProTheme {
        Surface {
            IconButtonSendMessage()
        }
    }
}