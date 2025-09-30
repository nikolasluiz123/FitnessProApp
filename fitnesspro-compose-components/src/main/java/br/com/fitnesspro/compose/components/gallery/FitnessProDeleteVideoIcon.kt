package br.com.fitnesspro.compose.components.gallery

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.android.ui.compose.components.video.components.DeleteVideoIcon
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_50

@Composable
fun FitnessProDeleteVideoIcon(
    hasThumbnail: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { }
) {
    DeleteVideoIcon(
        hasThumbnail = hasThumbnail,
        modifier = modifier,
        onClick = onClick,
        iconTintWithThumbnail = GREY_50,
        iconTintWithoutThumbnail = MaterialTheme.colorScheme.inverseOnSurface
    )
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun DeleteVideoIconPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.inverseSurface) {
            FitnessProDeleteVideoIcon(false)
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun DeleteVideoIconPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.inverseSurface) {
            FitnessProDeleteVideoIcon(false)
        }
    }
}