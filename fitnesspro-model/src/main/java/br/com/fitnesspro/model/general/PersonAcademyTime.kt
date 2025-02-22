package br.com.fitnesspro.model.general

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import java.time.DayOfWeek
import java.time.LocalDateTime
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
        )
    ],
    indices = [
        Index("person_id"),
        Index("academy_id")
    ]
)
data class PersonAcademyTime(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,
    @ColumnInfo(name = "person_id")
    var personId: String? = null,
    @ColumnInfo(name = "academy_id")
    var academyId: String? = null,
    @ColumnInfo(name = "time_start")
    var timeStart: LocalTime? = null,
    @ColumnInfo(name = "time_end")
    var timeEnd: LocalTime? = null,
    @ColumnInfo(name = "day_week")
    var dayOfWeek: DayOfWeek? = null,
    var active: Boolean = true,
): IntegratedModel()