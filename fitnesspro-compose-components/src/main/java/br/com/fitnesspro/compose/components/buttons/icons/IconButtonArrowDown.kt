package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.theme.GREY_400
import br.com.fitnesspro.core.theme.GREY_700

@Composable
fun IconButtonArrowDown(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_down_24dp),
            tint = GREY_700,
            contentDescription = null
        )
    }
}