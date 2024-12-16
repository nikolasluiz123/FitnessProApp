package br.com.fitnesspro.ui.screen.registeruser.to

import br.com.fitnesspro.to.BaseTO

data class TOAcademy(
    override var id: String? = null,
    val name: String? = null,
    var address: String? = null,
    var phone: String? = null,
): BaseTO()