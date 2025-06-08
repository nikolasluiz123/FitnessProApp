package br.com.fitnesspro.compose.components.gallery.video.components

import android.net.Uri
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryViewMode

val videoGalleryCollapsedEmptyState = VideoGalleryState(
    title = "Vídeos da Execução",
)

val videoGalleryExpandedEmptyState = VideoGalleryState(
    title = "Vídeos da Execução",
    viewMode = VideoGalleryViewMode.EXPANDED
)

val videoGalleryCollapsedWithOneValueState = VideoGalleryState(
    title = "Vídeos da Execução",
    viewMode = VideoGalleryViewMode.COLLAPSED,
    videoUris = listOf(Uri.fromParts("", "", ""))
)

val videoGalleryExpandedWithOneValueState = VideoGalleryState(
    title = "Vídeos da Execução",
    viewMode = VideoGalleryViewMode.EXPANDED,
    videoUris = listOf(Uri.fromParts("", "", ""))
)

val videoGalleryCollapsedManyValuesState = videoGalleryCollapsedWithOneValueState.copy(
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

val videoGalleryExpandedWithManyValuesState = videoGalleryExpandedWithOneValueState.copy(
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