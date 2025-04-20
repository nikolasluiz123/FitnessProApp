package br.com.fitnesspro.to

import java.time.LocalDate

data class TOPerson(
    override var id: String? = null,
    var name: String? = null,
    var birthDate: LocalDate? = null,
    var phone: String? = null,
    var user: TOUser? = null,
    var active: Boolean = true,
): BaseTO