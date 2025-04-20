package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
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
        ),
    ]
)
data class Workout(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    @ColumnInfo(name = "academy_member_person_id", index = true)
    var academyMemberPersonId: String? = null,
    @ColumnInfo(name = "personal_trainer_person_id", index = true)
    var professionalPersonId: String? = null,
    @ColumnInfo(name = "date_start")
    var dateStart: LocalDate? = null,
    @ColumnInfo(name = "date_end")
    var dateEnd: LocalDate? = null,
    var active: Boolean = true
): IntegratedModel