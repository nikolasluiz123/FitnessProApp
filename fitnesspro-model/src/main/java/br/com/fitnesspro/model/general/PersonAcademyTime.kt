package br.com.fitnesspro.model.general

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import java.time.LocalDateTime
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
    ]
)
data class PersonAcademyTime(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "person_id")
    var personId: String? = null,
    @ColumnInfo(name = "academy_id")
    var academyId: String? = null,
    @ColumnInfo(name = "date_time_start")
    var dateTimeStart: LocalDateTime? = null,
    @ColumnInfo(name = "date_time_end")
    var dateTimeEnd: LocalDateTime? = null,
    var active: Boolean = true,
): BaseModel()