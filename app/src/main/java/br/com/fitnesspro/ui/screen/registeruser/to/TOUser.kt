package br.com.fitnesspro.ui.screen.registeruser.to

import br.com.fitnesspro.core.annotation.TransferField
import br.com.fitnesspro.core.annotation.TransferObject
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.to.BaseTO

@TransferObject(User::class)
data class TOUser(
    override val id: String? = null,

    @TransferField("email")
    var email: String? = null,

    @TransferField("password")
    var password: String? = null,

    @TransferField("type")
    var type: EnumUserType? = null,

    @TransferField("active")
    var active: Boolean = true
): BaseTO()