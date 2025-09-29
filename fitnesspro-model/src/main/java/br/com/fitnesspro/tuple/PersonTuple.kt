package br.com.fitnesspro.tuple

import br.com.core.android.utils.interfaces.ISimpleListItem
import br.com.fitnesspro.model.enums.EnumUserType

data class PersonTuple(
    val id: String,
    val name: String,
    val userType: EnumUserType?
): ISimpleListItem {
    override fun getLabel(): String = name
}