package br.com.fitnesspro.compose.components.list.grouped.expandable

interface IBasicExpandableGroup<T> {
    val id: String
    val label: Int
    val value: String
    var isExpanded: Boolean
    val items: List<T>
}