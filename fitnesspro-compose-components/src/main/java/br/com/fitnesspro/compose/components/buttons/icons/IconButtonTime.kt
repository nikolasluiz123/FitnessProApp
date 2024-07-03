package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.theme.GREY_700

@Composable
fun IconButtonTime(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(
            painterResource(id = R.drawable.ic_time_24dp),
            tint = GREY_700,
            contentDescription = stringResource(br.com.fitnesspro.compose.components.R.string.label_hour)
        )
    }
}