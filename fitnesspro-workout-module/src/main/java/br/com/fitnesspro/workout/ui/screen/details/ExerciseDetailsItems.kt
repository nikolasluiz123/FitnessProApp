package br.com.fitnesspro.workout.ui.screen.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.LabeledText
import br.com.fitnesspro.compose.components.divider.FitnessProHorizontalDivider
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.formatToDecimal
import br.com.fitnesspro.core.extensions.toReadableDuration
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.LabelGroupTextStyle
import br.com.fitnesspro.tuple.ExerciseExecutionGroupedTuple
import br.com.fitnesspro.workout.R
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseExecutionItem(
    item: ExerciseExecutionGroupedTuple,
    onItemClick: (ExerciseExecutionGroupedTuple) -> Unit = {}
) {
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) }
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 3,
            maxLines = 2
        ) {
            val width = (LocalConfiguration.current.screenWidthDp - 32) / 3

            item.weight?.formatToDecimal()?.let {
                LabeledText(
                    label = stringResource(R.string.exercise_execution_label_weight),
                    value = it,
                    modifier = Modifier
                        .width(width.dp)
                        .padding(start = 8.dp, top = 8.dp)
                )
            }

            item.repetitions?.toString()?.let {
                LabeledText(
                    label = stringResource(R.string.exercise_execution_label_reps),
                    value = it,
                    modifier = Modifier
                        .width(width.dp)
                        .padding(start = 8.dp, top = 8.dp)
                )
            }

            item.duration?.toReadableDuration(context)?.let {
                LabeledText(
                    label = stringResource(R.string.exercise_execution_label_duration),
                    value = it,
                    modifier = Modifier
                        .width(width.dp)
                        .padding(start = 8.dp, top = 8.dp)
                )
            }

            item.rest?.toReadableDuration(context)?.let {
                LabeledText(
                    label = stringResource(R.string.exercise_execution_label_rest),
                    value = it,
                    modifier = Modifier
                        .width(width.dp)
                        .padding(start = 8.dp, top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        FitnessProHorizontalDivider()
    }
}

@Composable
fun ExecutionDateItem(date: LocalDate) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        Text(
            modifier = Modifier.align(alignment = Alignment.Center),
            text = date.format(EnumDateTimePatterns.DATE),
            style = LabelGroupTextStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun ExerciseExecutionItemLight1Preview() {
    FitnessProTheme {
        Surface {
            ExerciseExecutionItem(
                item = exerciseExecution1
            )
        }
    }
}

@Preview
@Composable
private fun ExerciseExecutionItemLight2Preview() {
    FitnessProTheme {
        Surface {
            ExerciseExecutionItem(
                item = exerciseExecution2
            )
        }
    }
}

@Preview
@Composable
private fun ExerciseExecutionItemLight3Preview() {
    FitnessProTheme {
        Surface {
            ExerciseExecutionItem(
                item = exerciseExecution3
            )
        }
    }
}

@Preview
@Composable
private fun ExecutionDateItemLightPreview() {
    FitnessProTheme {
        Surface {
            ExecutionDateItem(
                date = LocalDate.now()
            )
        }
    }
}

@Preview
@Composable
private fun ExerciseExecutionItemDark1Preview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseExecutionItem(
                item = exerciseExecution1
            )
        }
    }
}

@Preview
@Composable
private fun ExerciseExecutionItemDark2Preview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseExecutionItem(
                item = exerciseExecution2
            )
        }
    }
}

@Preview
@Composable
private fun ExerciseExecutionItemDark3Preview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseExecutionItem(
                item = exerciseExecution3
            )
        }
    }
}

@Preview
@Composable
private fun ExecutionDateItemDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExecutionDateItem(
                date = LocalDate.now()
            )
        }
    }
}