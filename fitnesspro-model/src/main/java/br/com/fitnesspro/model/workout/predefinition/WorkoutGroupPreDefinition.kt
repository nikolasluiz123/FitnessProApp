package br.com.fitnesspro.model.workout.predefinition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "workout_pre_definition"
)
data class WorkoutGroupPreDefinition(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    @ColumnInfo(name = "personal_trainer_person_id")
    var personalTrainerPersonId: String? = null,
    var active: Boolean = true
)