package br.com.fitnesspro.compose.components.gallery

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.android.ui.compose.components.video.components.PlayIconOverlay
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_50

@Composable
fun FitnessProPlayIconOverlay(hasThumbnail: Boolean, modifier: Modifier = Modifier) {
    PlayIconOverlay(
        hasThumbnail = hasThumbnail,
        modifier = modifier,
        iconTintWithThumbnail = GREY_50,
        iconTintWithoutThumbnail = MaterialTheme.colorScheme.inverseOnSurface
    )
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun PlayIconOverlayPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.inverseSurface) {
            FitnessProPlayIconOverlay(false)
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun PlayIconOverlayPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.inverseSurface) {
            FitnessProPlayIconOverlay(false)
        }
    }
}