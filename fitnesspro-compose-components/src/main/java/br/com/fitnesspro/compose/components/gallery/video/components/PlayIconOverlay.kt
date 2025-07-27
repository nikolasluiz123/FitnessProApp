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
import br.com.fitnesspro.core.theme.GREY_50

@Composable
fun PlayIconOverlay(hasThumbnail: Boolean) {
    val tint = if (hasThumbnail) {
        GREY_50
    } else {
        MaterialTheme.colorScheme.inverseOnSurface
    }

    Icon(
        painter = painterResource(R.drawable.ic_play_circle_filled_32dp),
        contentDescription = stringResource(R.string.play_icon_content_description),
        tint = tint,
    )
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun PlayIconOverlayPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.inverseSurface) {
            PlayIconOverlay(false)
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun PlayIconOverlayPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.inverseSurface) {
            PlayIconOverlay(false)
        }
    }
}