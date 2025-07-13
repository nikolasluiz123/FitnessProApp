package br.com.fitnesspro.compose.components.gallery.video.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.gallery.video.callbacks.OnVideoClick
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryViewMode
import br.com.fitnesspro.core.theme.FitnessProTheme

@Composable
internal fun ThumbnailsViewer(
    state: VideoGalleryState,
    onVideoClick: OnVideoClick?
) {
    when (state.viewMode) {
        VideoGalleryViewMode.COLLAPSED -> {
            VideosCarousel(state, onVideoClick)
        }

        VideoGalleryViewMode.EXPANDED -> {
            VideosGrid(state, onVideoClick)
        }
    }
}

@Composable
private fun VideosCarousel(state: VideoGalleryState, onVideoClick: OnVideoClick?) {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        userScrollEnabled = state.isScrollEnabled
    ) {
        items(state.videoPaths) { videoPath ->
            VideoThumbnail(
                bitmap = state.thumbCache[videoPath],
                onClick = { onVideoClick?.onExecute(videoPath) }
            )
        }
    }
}

@Composable
private fun VideosGrid(state: VideoGalleryState, onVideoClick: OnVideoClick?) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        items(state.videoPaths) { videoPath ->
            VideoThumbnail(
                bitmap = state.thumbCache[videoPath],
                onClick = { onVideoClick?.onExecute(videoPath) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun Modifier.appendScroll(state: VideoGalleryState): Modifier {
    return if (state.isScrollEnabled && state.videoPaths.isNotEmpty()) {
        horizontalScroll(rememberScrollState())
    } else {
        Modifier
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ThumbnailsViewerCollapsedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ThumbnailsViewer(
                state = videoGalleryCollapsedWithOneValueState,
                onVideoClick = {}
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ThumbnailsViewerCollapsedPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            ThumbnailsViewer(
                state = videoGalleryCollapsedWithOneValueState,
                onVideoClick = {}
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ThumbnailsViewerExpandedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ThumbnailsViewer(
                state = videoGalleryExpandedWithOneValueState,
                onVideoClick = {}
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ThumbnailsViewerExpandedManyValuesPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ThumbnailsViewer(
                state = videoGalleryExpandedWithManyValuesState,
                onVideoClick = {}
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ThumbnailsViewerExpandedPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            ThumbnailsViewer(
                state = videoGalleryExpandedWithOneValueState,
                onVideoClick = {}
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ThumbnailsViewerExpandedManyValuesPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            ThumbnailsViewer(
                state = videoGalleryExpandedWithManyValuesState,
                onVideoClick = {}
            )
        }
    }
}