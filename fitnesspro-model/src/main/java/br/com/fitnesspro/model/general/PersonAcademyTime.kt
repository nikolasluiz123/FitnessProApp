package br.com.fitnesspro.model.general

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID

@Entity(
    tableName = "person_academy_time",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["person_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Academy::class,
            parentColumns = ["id"],
            childColumns = ["academy_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class PersonAcademyTime(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    @ColumnInfo(name = "person_id", index = true)
    var personId: String? = null,
    @ColumnInfo(name = "academy_id", index = true)
    var academyId: String? = null,
    @ColumnInfo(name = "time_start")
    var timeStart: LocalTime? = null,
    @ColumnInfo(name = "time_end")
    var timeEnd: LocalTime? = null,
    @ColumnInfo(name = "day_week")
    var dayOfWeek: DayOfWeek? = null,
    var active: Boolean = true,
): IntegratedModel