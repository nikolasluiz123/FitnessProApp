package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.general.User
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "exercise",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutGroup::class,
            parentColumns = ["id"],
            childColumns = ["workout_group_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["creation_user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["update_user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("workout_group_id"),
        Index("creation_user_id"),
        Index("update_user_id")
    ]
)
data class Exercise(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,
	@ColumnInfo(name = "creation_date", defaultValue = "CURRENT_TIMESTAMP")
    override var creationDate: LocalDateTime = dateTimeNow(),
    @ColumnInfo(name = "update_date", defaultValue = "CURRENT_TIMESTAMP")
    override var updateDate: LocalDateTime = dateTimeNow(),
    @ColumnInfo(name = "creation_user_id")
    override var creationUserId: String? = null,
    @ColumnInfo(name = "update_user_id")
    override var updateUserId: String? = null,
    var name: String? = null,
    var duration: Long? = null,
    var repetitions: Int? = null,
    var sets: Int? = null,
    var rest: Long? = null,
    var observation: String? = null,
    @ColumnInfo(name = "workout_group_id")
    var workoutGroupId: String? = null,
    var active: Boolean = true
): IntegratedModel()