package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.general.Person
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "workout",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["academy_member_person_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["personal_trainer_person_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Workout(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "academy_member_person_id")
    var academyMemberPersonId: String? = null,
    @ColumnInfo(name = "personal_trainer_person_id")
    var professionalPersonId: String? = null,
    var start: LocalDate? = null,
    var end: LocalDate? = null,
    var active: Boolean = true
): BaseModel()