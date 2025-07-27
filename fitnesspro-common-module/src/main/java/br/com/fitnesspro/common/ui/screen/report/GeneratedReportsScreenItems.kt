package br.com.fitnesspro.common.ui.screen.report

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.toReadableFileSize
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.LabelTextStyle
import br.com.fitnesspro.core.theme.ValueTextStyle
import br.com.fitnesspro.to.TOReport

@Composable
fun GeneratedReportItem(
    toReport: TOReport,
    onItemClick: (TOReport) -> Unit = {},
    onDeleteClick: (TOReport) -> Unit = {}
) {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(toReport) }
    ) {
        val (nameRef, deleteButtonRef, dividerRef) = createRefs()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(nameRef) {
                    top.linkTo(parent.top, margin = 8.dp)
                    bottom.linkTo(dividerRef.top)
                    start.linkTo(parent.start, margin = 8.dp)
                    end.linkTo(deleteButtonRef.start)

                    width = Dimension.fillToConstraints
                }
        ) {
            Text(
                text = toReport.name!!,
                style = LabelTextStyle
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = getReportInformation(LocalContext.current, toReport),
                style = ValueTextStyle
            )
        }

        IconButtonDelete(
            iconColor = MaterialTheme.colorScheme.onBackground,
            iconModifier = Modifier.size(24.dp),
            onClick = { onDeleteClick(toReport) },
            modifier = Modifier.constrainAs(deleteButtonRef) {
                top.linkTo(parent.top)
                bottom.linkTo(dividerRef.top)
                end.linkTo(parent.end)
            }
        )

        HorizontalDivider(
            modifier = Modifier.constrainAs(dividerRef) {
                top.linkTo(nameRef.bottom, margin = 8.dp)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            color = MaterialTheme.colorScheme.outline
        )
    }
}

private fun getReportInformation(context: Context, report: TOReport): String {
    val date = report.date!!.format(EnumDateTimePatterns.DATE_TIME_SHORT)
    val size = report.kbSize!!.toReadableFileSize(context)

    return "$date - $size"
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun GeneratedReportItemDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            GeneratedReportItem(
                toReport = generatedReportsState.reports.first()
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun GeneratedReportItemPreview() {
    FitnessProTheme {
        Surface {
            GeneratedReportItem(
                toReport = generatedReportsState.reports.first()
            )
        }
    }
}