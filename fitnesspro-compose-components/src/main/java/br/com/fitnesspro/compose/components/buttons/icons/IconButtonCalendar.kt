package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.R.*
import br.com.fitnesspro.core.theme.GREY_700

@Composable
fun IconButtonCalendar(onClick: () -> Unit = { }) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = drawable.ic_calendar_24dp),
            tint = GREY_700,
            contentDescription = stringResource(R.string.label_calendar)
        )
    }
}