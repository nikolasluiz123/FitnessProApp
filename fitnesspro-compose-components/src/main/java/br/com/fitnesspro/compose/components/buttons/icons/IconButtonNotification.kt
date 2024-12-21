package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import br.com.fitnesspro.compose.components.R

@Composable
fun IconButtonNotification(onClick: () -> Unit = { }) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = br.com.fitnesspro.core.R.drawable.ic_notification_48dp),
            contentDescription = stringResource(R.string.label_notification),
        )
    }
}