package br.com.fitnesspro.to

import java.time.DayOfWeek
import java.time.LocalTime

data class TOPersonAcademyTime(
    override var id: String? = null,
    var personId: String? = null,
    var academyId: String? = null,
    var academyName: String? = null,
    var timeStart: LocalTime? = null,
    var timeEnd: LocalTime? = null,
    var dayOfWeek: DayOfWeek? = null,
    var active: Boolean = true,
): BaseTO