package br.com.fitnesspro.compose.components.gallery.video.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.divider.FitnessProHorizontalDivider
import br.com.fitnesspro.compose.components.gallery.video.callbacks.OnVideoClick
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryViewMode
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.VideoGalleryTitleStyle

@Composable
fun VideoGallery(
    state: VideoGalleryState,
    modifier: Modifier = Modifier,
    emptyMessage: String = stringResource(id = R.string.video_gallery_empty_message),
    onVideoClick: OnVideoClick? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val screenHeight = LocalWindowInfo.current.containerSize.height.dp

    val isExpanded = state.viewMode == VideoGalleryViewMode.EXPANDED
    val targetHeight = if (isExpanded) screenHeight else 210.dp

    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "GalleryHeight"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.extraSmall
            )
    ) {
        VideoGalleryHeader(
            title = state.title,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true)
        ) {
            if (state.videoPaths.isEmpty()) {
                EmptyState(emptyMessage)
            } else {
                ThumbnailsViewer(state, onVideoClick)
            }
        }

        FitnessProHorizontalDivider()

        ActionsBottomBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            state = state,
            actions = actions
        )
    }
}

@Composable
fun VideoGalleryHeader(title: String, modifier: Modifier) {
    Box(
        modifier
            .padding(top = 16.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = VideoGalleryTitleStyle
        )
    }
}


@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun VideoGalleryEmptyCollapsedPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            VideoGallery(
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
            VideoGallery(
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
            VideoGallery(
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
            VideoGallery(
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
            VideoGallery(
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
            VideoGallery(
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
            VideoGallery(
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
            VideoGallery(
                state = videoGalleryExpandedWithManyValuesState
            )
        }
    }
}