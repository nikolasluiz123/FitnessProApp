package br.com.fitnesspro.tuple

import br.com.core.android.utils.interfaces.ISimpleListItem

data class AcademyTuple(
    val id: String,
    val name: String,
): ISimpleListItem {
    override fun getLabel(): String = name
}