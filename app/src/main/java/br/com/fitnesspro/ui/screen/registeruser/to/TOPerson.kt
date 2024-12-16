package br.com.fitnesspro.ui.screen.registeruser.to

import br.com.fitnesspro.to.BaseTO
import java.time.LocalDate

data class TOPerson(
    override var id: String? = null,
    var name: String? = null,
    var birthDate: LocalDate? = null,
    var phone: String? = null,
    var toUser: TOUser? = null,
    var active: Boolean = true
): BaseTO()