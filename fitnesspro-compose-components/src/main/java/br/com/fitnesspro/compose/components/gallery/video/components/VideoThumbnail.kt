package br.com.fitnesspro.compose.components.gallery.video.components

import android.graphics.Bitmap
import android.net.Uri
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.core.theme.FitnessProTheme

@Composable
fun VideoThumbnail(
    uri: Uri,
    bitmap: Bitmap?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .width(180.dp)
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
                modifier = Modifier.fillMaxSize()
            )
        }
        PlayIconOverlay()
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun VideoThumbnailPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            VideoThumbnail(
                uri = Uri.fromParts("", "", ""),
                bitmap = null,
                onClick = {}
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun VideoThumbnailPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            VideoThumbnail(
                uri = Uri.fromParts("", "", ""),
                bitmap = null,
                onClick = {}
            )
        }
    }
}