package br.com.fitnesspro.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_700
import br.com.fitnesspro.core.theme.GREY_900
import br.com.fitnesspro.core.theme.LabelTextStyle
import br.com.fitnesspro.core.theme.ValueTextStyle
import java.util.UUID

@Composable
fun LabeledText(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    maxLinesValue: Int = Int.MAX_VALUE,
    overflowValue: TextOverflow = TextOverflow.Ellipsis,
    textAlign: TextAlign? = null
) {
    Column(modifier.wrapContentHeight()) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = label,
            style = LabelTextStyle,
            color = GREY_900,
            textAlign = textAlign
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            text = value,
            style = ValueTextStyle,
            color = GREY_700,
            maxLines = maxLinesValue,
            overflow = overflowValue,
            textAlign = textAlign
        )
    }
}

@Preview
@Composable
fun LabeledTextPreview() {
    FitnessProTheme {
        Surface {
            LabeledText(label = "Identificador", value = UUID.randomUUID().toString())
        }
    }
}