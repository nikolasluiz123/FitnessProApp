package br.com.fitnesspro.compose.components.gallery.video.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.theme.FitnessProTheme

@Composable
fun PlayIconOverlay() {
    Icon(
        painter = painterResource(R.drawable.ic_play_circle_filled_32dp),
        contentDescription = stringResource(R.string.play_icon_content_description),
        tint = MaterialTheme.colorScheme.inverseOnSurface,
    )
}

@Preview(device = "id:small_phone")
@Composable
private fun PlayIconOverlayPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.inverseSurface) {
            PlayIconOverlay()
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun PlayIconOverlayPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.inverseSurface) {
            PlayIconOverlay()
        }
    }
}