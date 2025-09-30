package br.com.fitnesspro.model.scheduler

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.interfaces.sync.IntegratedModel
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import java.time.OffsetDateTime
import java.util.UUID

@Entity(
    tableName = "scheduler",
)
data class Scheduler(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    @ColumnInfo(name = "academy_member_person_id", index = true)
    var academyMemberPersonId: String? = null,
    @ColumnInfo(name = "professional_person_id", index = true)
    var professionalPersonId: String? = null,
    @ColumnInfo(name = "date_time_start")
    var dateTimeStart: OffsetDateTime? = null,
    @ColumnInfo(name = "date_time_end")
    var dateTimeEnd: OffsetDateTime? = null,
    @ColumnInfo(name = "canceled_date")
    var canceledDate: OffsetDateTime? = null,
    @ColumnInfo(name = "cancellation_person_id", index = true)
    var cancellationPersonId: String? = null,
    var situation: EnumSchedulerSituation? = null,
    @ColumnInfo(name = "compromise_type")
    var compromiseType: EnumCompromiseType? = null,
    var observation: String? = null,
    var active: Boolean = true
): IntegratedModel