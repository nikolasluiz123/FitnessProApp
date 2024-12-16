package br.com.fitnesspro.ui.screen.registeruser.to

import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.BaseTO

data class TOUser(
    override var id: String? = null,
    var email: String? = null,
    var password: String? = null,
    var type: EnumUserType? = null,
    var active: Boolean = true
): BaseTO()