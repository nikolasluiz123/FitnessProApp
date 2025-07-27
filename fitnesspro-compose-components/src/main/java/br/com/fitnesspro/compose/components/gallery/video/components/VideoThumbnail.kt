package br.com.fitnesspro.compose.components.gallery.video.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.core.theme.FitnessProTheme

@Composable
fun VideoThumbnail(
    modifier: Modifier = Modifier,
    bitmap: Bitmap? = null,
    onClick: () -> Unit = { }
) {
    Box(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .width(120.dp)
            .height(90.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.onSurfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        PlayIconOverlay(
            hasThumbnail = bitmap != null
        )
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun VideoThumbnailPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            VideoThumbnail()
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun VideoThumbnailPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            VideoThumbnail()
        }
    }
}