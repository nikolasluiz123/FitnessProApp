package br.com.fitnesspro.tuple.reports.schedulers

import br.com.fitnesspro.model.enums.EnumCompromiseType
import java.time.OffsetDateTime

data class SchedulerReportTuple(
    val personName: String,
    val dateTimeStart: OffsetDateTime,
    val dateTimeEnd: OffsetDateTime,
    val compromiseType: EnumCompromiseType
)