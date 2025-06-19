package br.com.fitnesspro.compose.components.list.grouped.nested

import br.com.fitnesspro.compose.components.list.grouped.IBasicGroup

sealed class ListNode {
    data class GroupNode<T>(
        val group: IBasicGroup<T>,
        val depth: Int
    ) : ListNode()

    data class ItemNode<T>(
        val item: T,
        val depth: Int
    ) : ListNode()
}
