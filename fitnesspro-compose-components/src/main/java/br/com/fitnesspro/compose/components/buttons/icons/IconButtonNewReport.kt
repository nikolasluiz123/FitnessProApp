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
fun IconButtonNewReport(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.onPrimary,
    enabled: Boolean = true,
    contentDescriptionResId: Int? = R.string.label_new_report,
    onClick: () -> Unit = { }
) {
    FitnessProIconButton(
        modifier = modifier,
        iconModifier = iconModifier,
        resId = br.com.fitnesspro.core.R.drawable.ic_add_notes_24dp,
        iconColor = iconColor,
        enabled = enabled,
        contentDescriptionResId = contentDescriptionResId,
        onClick = onClick
    )
}



@Preview(device = "id:small_phone")
@Composable
fun IconButtonNewReportPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            IconButtonNewReport()
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun IconButtonNewReportPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            IconButtonNewReport()
        }
    }
}