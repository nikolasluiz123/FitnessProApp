package br.com.fitnesspro.compose.components.gallery

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.android.ui.compose.components.video.callbacks.OnVideoClick
import br.com.android.ui.compose.components.video.callbacks.OnVideoDeleteClick
import br.com.android.ui.compose.components.video.components.VideoGallery
import br.com.android.ui.compose.components.video.state.VideoGalleryState
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_50

@Composable
fun FitnessProVideoGallery(
    state: VideoGalleryState,
    modifier: Modifier = Modifier,
    emptyMessage: String = stringResource(id = R.string.video_gallery_empty_message),
    onVideoClick: OnVideoClick? = null,
    onVideoDeleteClick: OnVideoDeleteClick? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    VideoGallery(
        state = state,
        onVideoClick = onVideoClick,
        onVideoDeleteClick = onVideoDeleteClick,
        iconTintWithThumbnail = GREY_50,
        iconTintWithoutThumbnail = MaterialTheme.colorScheme.inverseOnSurface,
        emptyMessage = emptyMessage,
        modifier = modifier,
        actions = actions
    )
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun VideoGalleryEmptyCollapsedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProVideoGallery(
                state = videoGalleryCollapsedEmptyState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun VideoGalleryEmptyExpandedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProVideoGallery(
                state = videoGalleryExpandedEmptyState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun VideoGalleryExpandedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProVideoGallery(
                state = videoGalleryExpandedWithManyValuesState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun VideoGalleryCollapsedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProVideoGallery(
                state = videoGalleryCollapsedManyValuesState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun VideoGalleryEmptyExpandedPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProVideoGallery(
                state = videoGalleryExpandedEmptyState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun VideoGalleryEmptyCollapsedPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProVideoGallery(
                state = videoGalleryCollapsedEmptyState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun VideoGalleryCollapsedPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProVideoGallery(
                state = videoGalleryCollapsedManyValuesState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun VideoGalleryExpandedPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProVideoGallery(
                state = videoGalleryExpandedWithManyValuesState
            )
        }
    }
}