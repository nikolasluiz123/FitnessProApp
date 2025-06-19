package br.com.fitnesspro.compose.components.list.grouped.nested

import br.com.fitnesspro.compose.components.list.grouped.IBasicGroup

fun <T> List<IBasicGroup<T>>.flattenGroups(depth: Int = 0): List<ListNode> {
    return flatMap { group ->
        val header = ListNode.GroupNode(group, depth)
        val children = group.items.flatMap { item ->
            when (item) {
                is IBasicGroup<*> -> {
                    item.getAsGroupList().flattenGroups(depth + 1)
                }
                else -> {
                    listOf(ListNode.ItemNode(item, depth + 1))
                }
            }
        }
        listOf(header) + children
    }
}

private fun <T> IBasicGroup<T>.getAsGroupList(): List<IBasicGroup<T>> = listOf(this)
