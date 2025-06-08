package br.com.fitnesspro.compose.components.gallery.video.state

import android.graphics.Bitmap
import android.net.Uri

data class VideoGalleryState(
    val title: String = "",
    val videoUris: List<Uri> = emptyList(),
    val thumbCache: Map<Uri, Bitmap?> = emptyMap(),
    val isScrollEnabled: Boolean = true,
    val viewMode: VideoGalleryViewMode = VideoGalleryViewMode.COLLAPSED,
    val onViewModeChange: (VideoGalleryViewMode) -> Unit = { },
)