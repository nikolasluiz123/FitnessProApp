package br.com.fitnesspro.to

import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import java.time.LocalDateTime

data class TOScheduler(
    override var id: String? = null,
    var academyMemberPersonId: String? = null,
    var academyMemberName: String? = null,
    var professionalPersonId: String? = null,
    var professionalName: String? = null,
    var scheduledDate: LocalDateTime? = null,
    var canceledDate: LocalDateTime? = null,
    var situation: EnumSchedulerSituation? = null,
    var compromiseType: EnumCompromiseType? = null,
    var observation: String? = null,
    var active: Boolean = true
): BaseTO()