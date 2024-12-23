package br.com.fitnesspro.to

import java.time.DayOfWeek
import java.time.LocalTime

class TOPersonAcademyTime(
    override var id: String? = null,
    var personId: String? = null,
    var toAcademy: TOAcademy? = null,
    var timeStart: LocalTime? = null,
    var timeEnd: LocalTime? = null,
    var dayOfWeek: DayOfWeek? = null,
    var active: Boolean = true
): BaseTO()