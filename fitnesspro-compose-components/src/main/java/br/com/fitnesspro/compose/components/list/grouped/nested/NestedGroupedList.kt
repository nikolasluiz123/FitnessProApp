package br.com.fitnesspro.compose.components.list.grouped.nested

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import br.com.fitnesspro.compose.components.list.grouped.IBasicGroup
import br.com.fitnesspro.core.theme.LabelTextStyle

@Composable
fun <T: Any> NestedGroupedList(
    rootGroups: List<IBasicGroup<T>>,
    modifier: Modifier = Modifier,
    onGroup: @Composable (group: IBasicGroup<Any>, depth: Int) -> Unit,
    onItem: @Composable (item: Any, depth: Int) -> Unit,
    emptyMessageResId: Int
) {
    val nodes = remember(rootGroups) {
        rootGroups.flattenGroups()
    }

    if (nodes.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = emptyMessageResId),
                style = LabelTextStyle,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(modifier) {
            items(
                items = nodes,
                key = {
                    when (it) {
                        is ListNode.GroupNode<*> -> it.group.id
                        is ListNode.ItemNode<*> -> it.item.hashCode()
                    }
                },
                itemContent = { node ->
                    when (node) {
                        is ListNode.GroupNode<*> -> onGroup(node.group as IBasicGroup<Any>, node.depth)
                        is ListNode.ItemNode<*> -> onItem(node.item as Any, node.depth)
                    }
                }
            )
        }
    }
}
