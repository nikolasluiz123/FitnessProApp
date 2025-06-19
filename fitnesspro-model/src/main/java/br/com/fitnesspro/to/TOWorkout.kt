package br.com.fitnesspro.to

import java.time.LocalDate

data class TOWorkout(
    override var id: String? = null,
    var academyMemberPersonId: String? = null,
    var memberName: String? = null,
    var professionalPersonId: String? = null,
    var professionalName: String? = null,
    var dateStart: LocalDate? = null,
    var dateEnd: LocalDate? = null,
    var active: Boolean = true
): BaseTO