package br.com.fitnesspro.model.general.report

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.model.enums.EnumTransmissionState
import java.util.UUID

@Entity(
    tableName = "workout_report",
)
data class WorkoutReport(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    @ColumnInfo(name = "person_id", index = true)
    var personId: String? = null,
    @ColumnInfo(name = "workout_id", index = true)
    var workoutId: String? = null,
    @ColumnInfo(name = "report_id", index = true)
    var reportId: String? = null,
    @ColumnInfo(name = "report_context")
    var context: EnumReportContext? = null,
    var active: Boolean = true
): IntegratedModel