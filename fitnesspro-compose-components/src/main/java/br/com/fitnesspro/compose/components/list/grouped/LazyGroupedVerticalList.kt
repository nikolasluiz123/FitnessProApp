package br.com.fitnesspro.compose.components.list.grouped

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_800
import br.com.fitnesspro.core.theme.LabelTextStyle
import java.util.UUID

@Composable
fun <T, GROUP : IBasicGroup<T>> LazyGroupedVerticalList(
    groups: List<GROUP>,
    groupLayout: @Composable (GROUP) -> Unit,
    itemLayout: @Composable (T) -> Unit,
    emptyMessageResId: Int,
    modifier: Modifier = Modifier
) {
    if (groups.isNotEmpty()) {
        LazyColumn(
            modifier = modifier,
            content = {
                groups.forEach { group ->
                    item {
                        groupLayout(group)
                    }

                    items(group.items.size) { index ->
                        itemLayout(group.items[index])
                    }
                }
            }
        )
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = emptyMessageResId),
                style = LabelTextStyle,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

private class TestGroup(
    override val label: String,
    override val items: List<String>,
    override val id: String = UUID.randomUUID().toString()
) : IBasicGroup<String>

private val groups = mutableListOf(
    TestGroup(
        "Grupo 1",
        listOf("Teste 1.1", "Teste 1.2", "Teste 1.3")
    ),
    TestGroup(
        "Groupo 2",
        listOf("Teste 2.1", "Teste 2.2", "Teste 2.3")
    )
)

@Preview(device = "id:small_phone")
@Composable
private fun LazyGroupedVerticalListPreview() {
    FitnessProTheme {
        Surface {
            LazyGroupedVerticalList(
                modifier = Modifier.fillMaxSize(),
                groups = groups,
                emptyMessageResId = R.string.test_empty_message,
                groupLayout = {
                    Column(Modifier.padding(8.dp).fillMaxWidth()) {
                        Text(
                            text = it.label,
                            modifier = Modifier.background(color = GREY_800),
                            color = Color.White
                        )
                    }
                },
                itemLayout = {
                    Column(Modifier.padding(8.dp).fillMaxWidth()) {
                        Text(text = it)
                    }
                }
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun LazyGroupedVerticalListEmptyPreview() {
    FitnessProTheme {
        Surface {
            LazyGroupedVerticalList(
                groups = emptyList<TestGroup>(),
                emptyMessageResId = R.string.test_empty_message,
                groupLayout = {
                    Column(Modifier.padding(8.dp)) {
                        Text(
                            text = it.label,
                            modifier = Modifier.background(color = GREY_800),
                            color = Color.White
                        )
                    }
                },
                itemLayout = {
                    Column(Modifier.padding(8.dp)) {
                        Text(text = it)
                    }
                }
            )
        }
    }
}
