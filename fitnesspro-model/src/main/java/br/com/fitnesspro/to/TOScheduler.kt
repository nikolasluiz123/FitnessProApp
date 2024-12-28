package br.com.fitnesspro.to

import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class TOScheduler(
    override var id: String? = null,
    var academyMemberPersonId: String? = null,
    var academyMemberName: String? = null,
    var professionalPersonId: String? = null,
    var professionalName: String? = null,
    var scheduledDate: LocalDate? = null,
    var start: LocalTime? = null,
    var end: LocalTime? = null,
    var canceledDate: LocalDateTime? = null,
    var situation: EnumSchedulerSituation? = null,
    var compromiseType: EnumCompromiseType? = null,
    var observation: String? = null,
    var active: Boolean = true
): BaseTO()