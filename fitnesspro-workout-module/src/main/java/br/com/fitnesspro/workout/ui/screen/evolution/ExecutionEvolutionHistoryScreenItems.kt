package br.com.fitnesspro.workout.ui.screen.evolution

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.LabeledText
import br.com.fitnesspro.compose.components.divider.FitnessProHorizontalDivider
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.theme.LabelGroupTextStyle
import br.com.fitnesspro.tuple.ExecutionEvolutionHistoryGroupedTuple
import br.com.fitnesspro.workout.R

@Composable
fun ExecutionEvolutionListItem(tuple: ExecutionEvolutionHistoryGroupedTuple, onClick: (ExecutionEvolutionHistoryGroupedTuple) -> Unit = {}) {
    if (tuple.isGroup) {
        ExecutionEvolutionItemGroup(tuple)
    } else {
        ExecutionEvolutionItem(tuple, onClick)
    }
}

@Composable
private fun ExecutionEvolutionItemGroup(tuple: ExecutionEvolutionHistoryGroupedTuple) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        val start = tuple.dateStart?.format(EnumDateTimePatterns.DATE).orEmpty()
        val end = tuple.dateEnd?.format(EnumDateTimePatterns.DATE).orEmpty()

        Text(
            modifier = Modifier.align(alignment = Alignment.Center),
            text = "$start - $end",
            style = LabelGroupTextStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ExecutionEvolutionItem(tuple: ExecutionEvolutionHistoryGroupedTuple, onClick: (ExecutionEvolutionHistoryGroupedTuple) -> Unit = {}) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable {
                onClick(tuple)
            }
    ) {
        LabeledText(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            label = stringResource(R.string.execution_evolution_history_item_label_name),
            value = tuple.exerciseName.orEmpty()
        )

        FitnessProHorizontalDivider()
    }
}