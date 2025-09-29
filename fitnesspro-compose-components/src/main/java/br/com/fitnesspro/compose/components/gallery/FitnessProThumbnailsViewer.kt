package br.com.fitnesspro.compose.components.gallery

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.android.ui.compose.components.video.callbacks.OnVideoClick
import br.com.android.ui.compose.components.video.callbacks.OnVideoDeleteClick
import br.com.android.ui.compose.components.video.components.ThumbnailsViewer
import br.com.android.ui.compose.components.video.state.VideoGalleryState
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_50

@Composable
internal fun FitnessProThumbnailsViewer(
    state: VideoGalleryState,
    onVideoClick: OnVideoClick? = null,
    onVideoDeleteClick: OnVideoDeleteClick? = null
) {
    ThumbnailsViewer(
        state = state,
        onVideoClick = onVideoClick,
        onVideoDeleteClick = onVideoDeleteClick,
        iconTintWithThumbnail = GREY_50,
        iconTintWithoutThumbnail = MaterialTheme.colorScheme.inverseOnSurface
    )
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ThumbnailsViewerCollapsedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProThumbnailsViewer(
                state = videoGalleryCollapsedWithOneValueState,
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ThumbnailsViewerCollapsedPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProThumbnailsViewer(
                state = videoGalleryCollapsedWithOneValueState,
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ThumbnailsViewerExpandedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProThumbnailsViewer(
                state = videoGalleryExpandedWithOneValueState,
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ThumbnailsViewerExpandedManyValuesPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProThumbnailsViewer(
                state = videoGalleryExpandedWithManyValuesState,
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ThumbnailsViewerExpandedPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProThumbnailsViewer(
                state = videoGalleryExpandedWithOneValueState,
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ThumbnailsViewerExpandedManyValuesPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProThumbnailsViewer(
                state = videoGalleryExpandedWithManyValuesState,
            )
        }
    }
}