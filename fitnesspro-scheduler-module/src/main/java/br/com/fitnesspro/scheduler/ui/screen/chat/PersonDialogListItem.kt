package br.com.fitnesspro.scheduler.ui.screen.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fitnesspro.compose.components.divider.FitnessProHorizontalDivider
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.ValueTextStyle
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.compromisse.defaultPersonTuple
import br.com.fitnesspro.tuple.PersonTuple

@Composable
internal fun PersonDialogListItem(
    person: PersonTuple,
    onItemClick: (PersonTuple) -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(person)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val text = if (person.userType == EnumUserType.ACADEMY_MEMBER) {
            person.name
        } else {
            stringResource(
                R.string.compromise_screen_label_professional_name_and_type,
                person.name,
                person.userType?.getLabel(LocalContext.current)!!
            )
        }

        Text(
            modifier = Modifier
                .padding(12.dp),
            text = text,
            style = ValueTextStyle.copy(fontSize = 16.sp)
        )
    }

    FitnessProHorizontalDivider()
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun PersonDialogListItemPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            PersonDialogListItem(
                person = defaultPersonTuple
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun PersonDialogListItemPreviewLight() {
    FitnessProTheme {
        Surface {
            PersonDialogListItem(
                person = defaultPersonTuple
            )
        }
    }
}