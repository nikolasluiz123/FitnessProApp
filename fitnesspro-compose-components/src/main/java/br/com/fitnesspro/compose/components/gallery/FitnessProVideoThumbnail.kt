package br.com.fitnesspro.compose.components.gallery

import android.graphics.Bitmap
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.android.ui.compose.components.video.components.VideoThumbnail
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_50

@Composable
fun FitnessProVideoThumbnail(
    modifier: Modifier = Modifier,
    bitmap: Bitmap? = null,
    showDeleteButton: Boolean = true,
    onVideoClick: () -> Unit = { },
    onDeleteVideoClick: () -> Unit = { }
) {
    VideoThumbnail(
        modifier = modifier,
        bitmap = bitmap,
        showDeleteButton = showDeleteButton,
        onVideoClick = onVideoClick,
        onDeleteVideoClick = onDeleteVideoClick,
        iconTintWithThumbnail = GREY_50,
        iconTintWithoutThumbnail = MaterialTheme.colorScheme.inverseOnSurface
    )
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun VideoThumbnailPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProVideoThumbnail()
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun VideoThumbnailPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProVideoThumbnail()
        }
    }
}