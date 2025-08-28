package br.com.fitnesspro.compose.components.gallery.video.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_50

@Composable
fun DeleteVideoIcon(hasThumbnail: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit = { }) {
    val tint = if (hasThumbnail) {
        GREY_50
    } else {
        MaterialTheme.colorScheme.inverseOnSurface
    }

    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_cancel_24dp),
            contentDescription = stringResource(R.string.delete_icon_content_description),
            tint = tint,
        )
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun DeleteVideoIconPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.inverseSurface) {
            DeleteVideoIcon(false)
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun DeleteVideoIconPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.inverseSurface) {
            DeleteVideoIcon(false)
        }
    }
}