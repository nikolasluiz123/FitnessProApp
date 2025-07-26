package br.com.fitnesspro.model.general.report

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.general.Person
import java.util.UUID

@Entity(
    tableName = "scheduler_report",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["person_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Report::class,
            parentColumns = ["id"],
            childColumns = ["report_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SchedulerReport(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    @ColumnInfo(name = "person_id", index = true)
    var personId: String? = null,
    @ColumnInfo(name = "report_id", index = true)
    var reportId: String? = null,
    @ColumnInfo(name = "report_context")
    var context: EnumReportContext? = null
): IntegratedModel