package br.com.fitnesspro.compose.components.list.expandable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.compose.components.LabeledText
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.R.drawable
import br.com.fitnesspro.core.theme.FitnessProTheme

@Composable
fun <T, GROUP : IBasicExpandableGroup<T>> LazyExpandableVerticalList(
    groups: List<GROUP>,
    itemLayout: @Composable (T) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        content = {
            groups.onEach { group ->
                item {
                    BasicExpandableSection(
                        label = stringResource(id = group.label),
                        value = group.value,
                        isExpanded = group.isExpanded,
                        onClick = { group.isExpanded = !group.isExpanded }
                    )
                }

                items(group.items.size) { index ->
                    AnimatedVisibility(visible = group.isExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                        itemLayout(group.items[index])
                    }
                }
            }
        }
    )
}

@Composable
fun BasicExpandableSection(
    label: String,
    value: String,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { }
) {
    val transition = updateTransition(targetState = isExpanded, label = "expandTransition")

    val arrowRotation by transition.animateFloat(
        label = "arrowRotation",
        transitionSpec = { tween(durationMillis = Spring.StiffnessMediumLow.toInt()) }
    ) { expanded ->
        if (expanded) 180f else 0f
    }

    ConstraintLayout(modifier.clickable { onClick() }) {
        val (textRef, iconRef, dividerRef) = createRefs()

        LabeledText(
            modifier = Modifier.constrainAs(textRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            label = label,
            value = value
        )

        Icon(
            modifier = Modifier
                .constrainAs(iconRef) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .padding(end = 8.dp)
                .rotate(arrowRotation),
            painter = painterResource(id =  drawable.ic_expand_more),
            contentDescription = null
        )

        HorizontalDivider(
            modifier = Modifier.constrainAs(dividerRef) {
                bottom.linkTo(parent.bottom)
                top.linkTo(textRef.bottom, margin = 8.dp)
            }
        )
    }
}

private class TestGroup(
    override val label: Int,
    override val value: String,
    override var isExpanded: Boolean,
    override val items: List<String>
) : IBasicExpandableGroup<String>

private val groups = mutableListOf(
    TestGroup(
        R.string.test_group_1,
        "Teste 1",
        false,
        listOf("Teste 1.1", "Teste 1.2", "Teste 1.3")
    ),
    TestGroup(
        R.string.test_group_2,
        "Teste 2",
        false,
        listOf("Teste 2.1", "Teste 2.2", "Teste 2.3")
    )
)

private val groupsExpanded = mutableListOf(
    TestGroup(
        R.string.test_group_1,
        "Teste 1",
        true,
        listOf("Teste 1.1", "Teste 1.2", "Teste 1.3")
    ),
    TestGroup(
        R.string.test_group_2,
        "Teste 2",
        true,
        listOf("Teste 2.1", "Teste 2.2", "Teste 2.3")
    )
)

@Preview
@Composable
private fun ExpandableListExpandedPreview() {
    FitnessProTheme {
        Surface {
            LazyExpandableVerticalList(
                groups = groupsExpanded,
                itemLayout = {
                    Column(Modifier.padding(8.dp)) {
                        Text(text = it)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun ExpandableListPreview() {
    FitnessProTheme {
        Surface {
            LazyExpandableVerticalList(
                groups = groups,
                itemLayout = {
                    Column(Modifier.padding(8.dp)) {
                        Text(text = it)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun ExpandableSectionPreview() {
    FitnessProTheme {
        Surface {
            BasicExpandableSection("Teste", "Testando", false)
        }
    }
}

