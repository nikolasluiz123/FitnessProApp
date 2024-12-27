package br.com.fitnesspro.model.scheduler

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "scheduler")
data class Scheduler(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "academy_member_person_id")
    var academyMemberPersonId: String? = null,
    @ColumnInfo(name = "professional_person_id")
    var professionalPersonId: String? = null,
    @ColumnInfo(name = "scheduled_date")
    var scheduledDate: LocalDateTime? = null,
    @ColumnInfo(name = "canceled_date")
    var canceledDate: LocalDateTime? = null,
    var situation: EnumSchedulerSituation? = null,
    @ColumnInfo(name = "compromise_type")
    var compromiseType: EnumCompromiseType? = null,
    var observation: String? = null,
    var active: Boolean = true
): BaseModel()