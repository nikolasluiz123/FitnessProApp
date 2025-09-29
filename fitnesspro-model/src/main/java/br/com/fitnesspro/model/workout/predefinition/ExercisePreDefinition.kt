package br.com.fitnesspro.model.workout.predefinition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.interfaces.sync.IntegratedModel
import java.util.UUID

@Entity(
    tableName = "exercise_pre_definition",
)
data class ExercisePreDefinition(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    var name: String? = null,
    var duration: Long? = null,
    var repetitions: Int? = null,
    var sets: Int? = null,
    var rest: Long? = null,
    @ColumnInfo(name = "exercise_order")
    var exerciseOrder: Int? = null,
    @ColumnInfo(name = "personal_trainer_person_id", index = true)
    var personalTrainerPersonId: String? = null,
    @ColumnInfo(name = "workout_group_pre_definition_id", index = true)
    var workoutGroupPreDefinitionId: String? = null,
    var active: Boolean = true
): IntegratedModel