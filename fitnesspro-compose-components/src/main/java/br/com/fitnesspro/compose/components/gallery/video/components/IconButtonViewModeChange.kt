package br.com.fitnesspro.compose.components.gallery.video.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryViewMode
import br.com.fitnesspro.core.R

@Composable
internal fun IconButtonViewModeChange(state: VideoGalleryState) {
    IconButton(
        onClick = {
            val viewMode = getNewViewModeBy(state)
            state.onViewModeChange(viewMode)
        }
    ) {
        Icon(
            painter = getVideoGalleryIconViewModelChange(state),
            contentDescription = null
        )
    }
}

internal fun getNewViewModeBy(state: VideoGalleryState): VideoGalleryViewMode {
    return when (state.viewMode) {
        VideoGalleryViewMode.COLLAPSED -> VideoGalleryViewMode.EXPANDED
        VideoGalleryViewMode.EXPANDED -> VideoGalleryViewMode.COLLAPSED
    }
}

@Composable
private fun getVideoGalleryIconViewModelChange(state: VideoGalleryState): Painter {
    return if (state.viewMode == VideoGalleryViewMode.COLLAPSED) {
        painterResource(R.drawable.ic_expand_24dp)
    } else {
        painterResource(R.drawable.ic_compress_24dp)
    }
}