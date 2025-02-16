package br.com.fitnesspro.to

import br.com.fitnesspro.model.enums.EnumUserType

data class TOUser(
    override var id: String? = null,
    var email: String? = null,
    var password: String? = null,
    var type: EnumUserType? = null,
    var active: Boolean = true,
    var serviceToken: String? = null
): BaseTO()