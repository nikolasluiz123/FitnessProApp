package br.com.fitnesspro.to

import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.enums.EnumUserType
import java.time.OffsetDateTime

data class TOScheduler(
    override var id: String? = null,
    var academyMemberPersonId: String? = null,
    var academyMemberName: String? = null,
    var professionalPersonId: String? = null,
    var professionalName: String? = null,
    var professionalType: EnumUserType? = null,
    var dateTimeStart: OffsetDateTime? = null,
    var dateTimeEnd: OffsetDateTime? = null,
    var canceledDate: OffsetDateTime? = null,
    var cancellationPersonId: String? = null,
    var cancellationPersonName: String? = null,
    var situation: EnumSchedulerSituation? = null,
    var compromiseType: EnumCompromiseType? = null,
    var observation: String? = null,
    var active: Boolean = true,
): BaseTO