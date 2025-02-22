package br.com.fitnesspro.model.workout.predefinition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.general.Person
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "exercise_pre_definition",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["personal_trainer_person_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkoutGroupPreDefinition::class,
            parentColumns = ["id"],
            childColumns = ["workout_group_pre_definition_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("personal_trainer_person_id"),
        Index("workout_group_pre_definition_id")
    ]
)
data class ExercisePreDefinition(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,
    var name: String? = null,
    var duration: Long? = null,
    var repetitions: Int? = null,
    var series: Int? = null,
    var rest: Long? = null,
    @ColumnInfo(name = "personal_trainer_person_id")
    var personalTrainerPersonId: String? = null,
    @ColumnInfo(name = "workout_group_pre_definition_id")
    var workoutGroupPreDefinitionId: String? = null,
    var active: Boolean = true
): IntegratedModel()