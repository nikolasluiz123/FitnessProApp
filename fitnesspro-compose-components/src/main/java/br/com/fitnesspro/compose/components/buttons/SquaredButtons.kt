package br.com.fitnesspro.compose.components.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_300
import br.com.fitnesspro.core.theme.GREY_700
import br.com.fitnesspro.core.theme.ValueTextStyle

@Composable
fun SquaredButton(
    iconResId: Int,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    Card(
        modifier
            .fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = if (enabled) Color.White else GREY_300),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 16.dp else 0.dp),
        shape = RoundedCornerShape(4.dp),
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .clickable(onClick = onClick, enabled = enabled)
        ) {
            val (columnRef) = createRefs()

            Column(
                Modifier.constrainAs(columnRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .alpha(if (enabled) 1f else 0.5f),
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                )

                Text(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .alpha(if (enabled) 1f else 0.5f),
                    text = label,
                    style = ValueTextStyle,
                    color = GREY_700
                )
            }
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun SquaredButtonPreview() {
    FitnessProTheme {
        Surface {
            SquaredButton(
                modifier = Modifier,
                iconResId = br.com.fitnesspro.core.R.drawable.ic_calendar_32dp,
                label = "Agenda"
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun SquaredButtonDisabledPreview() {
    FitnessProTheme {
        Surface {
            SquaredButton(
                iconResId = br.com.fitnesspro.core.R.drawable.ic_calendar_32dp,
                label = "Agenda",
                enabled = false
            )
        }
    }
}