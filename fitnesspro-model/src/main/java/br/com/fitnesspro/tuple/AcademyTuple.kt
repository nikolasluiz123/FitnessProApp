package br.com.fitnesspro.tuple

import br.com.fitnesspro.core.menu.ITupleListItem

data class AcademyTuple(
    val id: String,
    val name: String,
): ITupleListItem {
    override fun getLabel(): String = name
}