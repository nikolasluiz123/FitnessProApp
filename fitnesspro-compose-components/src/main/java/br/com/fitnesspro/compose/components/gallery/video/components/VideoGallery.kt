package br.com.fitnesspro.compose.components.gallery.video.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryViewMode
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.VideoGalleryTitleStyle

private const val VIDEO_GALLERY_TRANSITION = "VideoGalleryTransition"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun VideoGallery(
    state: VideoGalleryState,
    modifier: Modifier = Modifier,
    emptyMessage: String = stringResource(id = R.string.video_gallery_empty_message),
    onVideoClick: (Uri) -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.extraSmall
            )
    ) {
        val (headerRef, contentRef, dividerRef, actionsRef) = createRefs()

        val isExpanded = state.viewMode == VideoGalleryViewMode.EXPANDED

        VideoGalleryHeader(
            title = state.title,
            modifier = Modifier
                .constrainAs(headerRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        AnimatedContent(
            targetState = state.viewMode,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)).togetherWith(fadeOut(animationSpec = tween(300)))
            },
            modifier = Modifier
                .then(Modifier.defineSize(isExpanded))
                .constrainAs(contentRef) {
                    top.linkTo(headerRef.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(dividerRef.top)
                },
            label = VIDEO_GALLERY_TRANSITION
        ) { mode ->
            if (state.videoUris.isEmpty()) {
                EmptyState(emptyMessage)
            } else {
                ThumbnailsViewer(mode, state, onVideoClick)
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .constrainAs(dividerRef) {
                    bottom.linkTo(actionsRef.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        ActionsBottomBar(
            modifier = Modifier
                .constrainAs(actionsRef) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
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
            .fillMaxWidth()
            .padding(top = 16.dp, start = 8.dp, end = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = VideoGalleryTitleStyle
        )
    }
}

@Composable
private fun Modifier.defineSize(isExpanded: Boolean): Modifier {
    return if (isExpanded) {
        fillMaxWidth().heightIn(min = 120.dp)
    } else {
        fillMaxWidth().height(120.dp)
    }
}

@Preview(device = "id:small_phone")
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

@Preview(device = "id:small_phone")
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

@Preview(device = "id:small_phone")
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

@Preview(device = "id:small_phone")
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

@Preview(device = "id:small_phone")
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

@Preview(device = "id:small_phone")
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

@Preview(device = "id:small_phone")
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

@Preview(device = "id:small_phone")
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
