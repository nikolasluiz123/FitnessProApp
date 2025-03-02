package br.com.fitnesspro.model.scheduler

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.general.Person
import java.util.UUID


@Entity(
    tableName = "scheduler_config",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["person_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ]
)
data class SchedulerConfig(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    var alarm: Boolean = false,
    var notification: Boolean = false,
    @ColumnInfo(name = "min_schedule_density")
    var minScheduleDensity: Int = 1,
    @ColumnInfo(name = "max_schedule_density")
    var maxScheduleDensity: Int = 2,
    @ColumnInfo(name = "person_id", index = true)
    var personId: String? = null
): IntegratedModel