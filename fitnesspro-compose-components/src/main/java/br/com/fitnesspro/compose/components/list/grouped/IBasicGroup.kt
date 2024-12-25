package br.com.fitnesspro.compose.components.list.grouped

interface IBasicGroup<T> {
    val id: String
    val label: String
    val items: List<T>
}