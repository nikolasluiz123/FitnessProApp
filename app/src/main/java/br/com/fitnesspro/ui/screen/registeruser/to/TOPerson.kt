package br.com.fitnesspro.ui.screen.registeruser.to

import br.com.fitnesspro.core.annotation.TransferField
import br.com.fitnesspro.core.annotation.TransferObject
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.to.BaseTO
import java.time.LocalDate

@TransferObject(Person::class)
data class TOPerson(
    override val id: String? = null,

    @TransferField("name")
    var name: String? = null,

    @TransferField("birthDate")
    var birthDate: LocalDate? = null,

    @TransferField("phone")
    var phone: String? = null,

    @TransferField("userId")
    var toUser: TOUser? = null,

    @TransferField("active")
    var active: Boolean = true
): BaseTO()