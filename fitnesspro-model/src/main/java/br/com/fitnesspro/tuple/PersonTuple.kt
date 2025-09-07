package br.com.fitnesspro.tuple

import br.com.fitnesspro.core.menu.ITupleListItem
import br.com.fitnesspro.model.enums.EnumUserType

data class PersonTuple(
    val id: String,
    val name: String,
    val userType: EnumUserType?
): ITupleListItem {
    override fun getLabel(): String = name
}