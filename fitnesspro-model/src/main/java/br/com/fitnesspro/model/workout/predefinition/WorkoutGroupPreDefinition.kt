package br.com.fitnesspro.model.workout.predefinition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.general.Person
import java.util.UUID

@Entity(
    tableName = "workout_group_pre_definition",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["personal_trainer_person_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutGroupPreDefinition(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    var name: String? = null,
    @ColumnInfo(name = "personal_trainer_person_id", index = true)
    var personalTrainerPersonId: String? = null,
    var active: Boolean = true
): IntegratedModel