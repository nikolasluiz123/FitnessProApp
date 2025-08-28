package br.com.fitnesspro.compose.components.gallery.video.state

import android.graphics.Bitmap

data class VideoGalleryState(
    val title: String = "",
    val videoPaths: List<String> = emptyList(),
    val thumbCache: Map<String, Bitmap?> = emptyMap(),
    val isScrollEnabled: Boolean = true,
    val viewMode: VideoGalleryViewMode = VideoGalleryViewMode.COLLAPSED,
    val onViewModeChange: (VideoGalleryViewMode) -> Unit = { },
    val showDeleteButton: Boolean = true,
)