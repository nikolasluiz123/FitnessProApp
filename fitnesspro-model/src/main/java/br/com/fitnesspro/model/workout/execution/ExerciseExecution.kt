package br.com.fitnesspro.model.workout.execution

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.workout.Exercise
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "exercise_execution",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("exercise_id")
    ]
)
data class ExerciseExecution(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "exercise_id")
    var exerciseId: String? = null,
    var duration: Long? = null,
    var repetitions: Int? = null,
    var set: Int? = null,
    var rest: Long? = null,
    var weight: Double? = null,
    var date: LocalDateTime = LocalDateTime.now(),
    var active: Boolean = true
): BaseModel()