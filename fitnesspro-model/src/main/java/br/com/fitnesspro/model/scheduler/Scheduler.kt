package br.com.fitnesspro.model.scheduler

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.general.Person
import java.time.OffsetDateTime
import java.util.UUID

@Entity(
    tableName = "scheduler",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["academy_member_person_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["professional_person_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["cancellation_person_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
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