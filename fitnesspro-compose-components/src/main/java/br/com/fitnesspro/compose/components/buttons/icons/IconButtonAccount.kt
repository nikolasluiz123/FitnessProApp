package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.theme.FitnessProTheme

@Composable
fun IconButtonAccount(onClick: () -> Unit = { }) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(br.com.fitnesspro.core.R.drawable.ic_account_circle_filled_24dp),
            contentDescription = stringResource(R.string.label_count)
        )
    }
}


@Preview
@Composable
fun IconButtonAccountPreview() {
    FitnessProTheme {
        Surface {
            IconButtonAccount()
        }
    }
}