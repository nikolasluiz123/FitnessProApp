package br.com.fitnesspro.compose.components.gallery.video.components

import android.net.Uri
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryViewMode

internal val videoGalleryCollapsedEmptyState = VideoGalleryState(
    title = "Vídeos da Execução",
)

internal val videoGalleryExpandedEmptyState = VideoGalleryState(
    title = "Vídeos da Execução",
    viewMode = VideoGalleryViewMode.EXPANDED
)

internal val videoGalleryCollapsedWithOneValueState = VideoGalleryState(
    title = "Vídeos da Execução",
    viewMode = VideoGalleryViewMode.COLLAPSED,
    videoUris = listOf(Uri.fromParts("", "", ""))
)

internal val videoGalleryExpandedWithOneValueState = VideoGalleryState(
    title = "Vídeos da Execução",
    viewMode = VideoGalleryViewMode.EXPANDED,
    videoUris = listOf(Uri.fromParts("", "", ""))
)

internal val videoGalleryCollapsedManyValuesState = videoGalleryCollapsedWithOneValueState.copy(
    videoUris = listOf(
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
    )
)

internal val videoGalleryExpandedWithManyValuesState = videoGalleryExpandedWithOneValueState.copy(
    videoUris = listOf(
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
        Uri.fromParts("", "", ""),
    )
)