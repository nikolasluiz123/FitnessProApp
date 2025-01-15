package br.com.fitnesspro.model.scheduler

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.general.Person
import java.time.LocalTime
import java.util.UUID


@Entity(
    tableName = "scheduler_config",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["person_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index("person_id")
    ]
)
data class SchedulerConfig(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    var alarm: Boolean = false,
    var notification: Boolean = false,
    @ColumnInfo(name = "min_schedule_density")
    var minScheduleDensity: Int = 1,
    @ColumnInfo(name = "max_schedule_density")
    var maxScheduleDensity: Int = 2,
    @ColumnInfo(name = "person_id")
    var personId: String? = null,
    @ColumnInfo(name = "start_work_time", defaultValue = "08:00")
    var startWorkTime: LocalTime = LocalTime.of(8, 0),
    @ColumnInfo(name = "end_work_time", defaultValue = "17:30")
    var endWorkTime: LocalTime = LocalTime.of(17, 30),
    @ColumnInfo(name = "start_break_time", defaultValue = "12:00")
    var startBreakTime: LocalTime = LocalTime.of(12, 0),
    @ColumnInfo(name = "end_break_time", defaultValue = "13:00")
    var endBreakTime: LocalTime = LocalTime.of(13, 0),
): BaseModel()