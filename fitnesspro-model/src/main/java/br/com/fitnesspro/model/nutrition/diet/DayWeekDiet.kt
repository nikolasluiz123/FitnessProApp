package br.com.fitnesspro.model.nutrition.diet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.interfaces.sync.IntegratedModel
import java.time.DayOfWeek
import java.util.UUID

@Entity(
    tableName = "day_week_foods",
)
data class DayWeekDiet(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    @ColumnInfo(name = "day_week")
    var dayWeek: DayOfWeek? = null,
    @ColumnInfo(name = "diet_id", index = true)
    var dietId: String? = null,
    var active: Boolean = true
): IntegratedModel