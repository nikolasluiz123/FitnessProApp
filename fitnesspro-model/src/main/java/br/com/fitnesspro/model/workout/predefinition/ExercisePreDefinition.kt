package br.com.fitnesspro.model.workout.predefinition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.general.Person
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
    ]
)
data class ExercisePreDefinition(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
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
)