package br.com.fitnesspro.compose.components.gallery

import br.com.android.ui.compose.components.video.state.VideoGalleryState
import br.com.android.ui.compose.components.video.state.VideoGalleryViewMode

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
    videoPaths = listOf("")
)

val videoGalleryExpandedWithOneValueState = VideoGalleryState(
    title = "Vídeos da Execução",
    viewMode = VideoGalleryViewMode.EXPANDED,
    videoPaths = listOf("")
)

val videoGalleryCollapsedManyValuesState = videoGalleryCollapsedWithOneValueState.copy(
    videoPaths = listOf("", "", "", "", "", "", "", "")
)

val videoGalleryExpandedWithManyValuesState = videoGalleryExpandedWithOneValueState.copy(
    videoPaths = listOf("", "", "", "", "", "", "", "")
)