package br.com.fitnesspro.model.scheduler

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.interfaces.sync.IntegratedModel
import java.util.UUID


@Entity(
    tableName = "scheduler_config",
)
data class SchedulerConfig(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    var notification: Boolean = false,
    @ColumnInfo(name = "notification_antecedence_time")
    var notificationAntecedenceTime: Int = 30,
    @ColumnInfo(name = "min_schedule_density")
    var minScheduleDensity: Int = 1,
    @ColumnInfo(name = "max_schedule_density")
    var maxScheduleDensity: Int = 2,
    @ColumnInfo(name = "person_id", index = true)
    var personId: String? = null
): IntegratedModel