package br.com.fitnesspro.workout.ui.screen.evolution

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.android.ui.compose.components.divider.BaseHorizontalDivider
import br.com.android.ui.compose.components.label.LabeledText
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.tuple.PersonTuple
import br.com.fitnesspro.workout.R

@Composable
fun MemberItem(personTuple: PersonTuple, onClick: (PersonTuple) -> Unit = {}) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onClick(personTuple) }
    ) {
        LabeledText(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            label = stringResource(R.string.member_item_label_name),
            value = personTuple.name,
        )

        BaseHorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}

@Preview
@Composable
private fun MemberItemLightPreview() {
    FitnessProTheme {
        Surface {
            MemberItem(
                personTuple = defaultPersonTuple
            )
        }
    }
}


@Preview
@Composable
private fun MemberItemPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            MemberItem(
                personTuple = defaultPersonTuple
            )
        }
    }
}