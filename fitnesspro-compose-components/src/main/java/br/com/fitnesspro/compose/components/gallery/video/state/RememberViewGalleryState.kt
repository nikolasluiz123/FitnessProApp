package br.com.fitnesspro.compose.components.gallery.video.state

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberVideoGalleryState(
    videoUris: List<Uri> = emptyList(),
    thumbCache: Map<Uri, Bitmap?> = emptyMap(),
    isScrollEnabled: Boolean = true,
    viewMode: VideoGalleryViewMode = VideoGalleryViewMode.COLLAPSED,
    onViewModeChange: (VideoGalleryViewMode) -> Unit = {}
): VideoGalleryState = remember {
    VideoGalleryState(
        videoUris = videoUris,
        thumbCache = thumbCache,
        isScrollEnabled = isScrollEnabled,
        viewMode = viewMode,
        onViewModeChange = onViewModeChange
    )
}