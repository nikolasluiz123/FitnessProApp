package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.theme.FitnessProTheme

@Composable
fun IconButtonMessage(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.onPrimary,
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



@Preview(device = "id:small_phone")
@Composable
fun IconButtonMessagePreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            IconButtonMessage()
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun IconButtonMessagePreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            IconButtonMessage()
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun IconButtonMessageWithMessagesPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            IconButtonMessage(hasMessagesToRead = true)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun IconButtonMessageWithMessagesPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            IconButtonMessage(hasMessagesToRead = true)
        }
    }
}