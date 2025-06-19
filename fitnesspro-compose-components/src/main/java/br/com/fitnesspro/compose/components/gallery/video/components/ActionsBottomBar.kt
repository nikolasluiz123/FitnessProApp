package br.com.fitnesspro.compose.components.gallery.video.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState

@Composable
internal fun ActionsBottomBar(
    modifier: Modifier,
    state: VideoGalleryState,
    actions: @Composable (RowScope.() -> Unit)
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButtonViewModeChange(state)

        Spacer(modifier = Modifier.weight(1f))

        Row(content = actions)
    }
}