package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.theme.FitnessProTheme

@Composable
fun IconButtonCamera(
    modifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.onPrimary,
    enabled: Boolean = true,
    contentDescriptionResId: Int? = R.string.label_camera,
    onClick: () -> Unit = { }
) {
    FitnessProIconButton(
        modifier = modifier,
        resId = br.com.fitnesspro.core.R.drawable.ic_camera_alt_24dp,
        iconColor = iconColor,
        enabled = enabled,
        contentDescriptionResId = contentDescriptionResId,
        onClick = onClick
    )
}


@Preview(device = "id:small_phone")
@Composable
fun IconButtonCameraPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            IconButtonCamera()
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun IconButtonCameraPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            IconButtonCamera()
        }
    }
}