package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.general.User
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "workout_group",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
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
        Index("workout_id"),
        Index("creation_user_id"),
        Index("update_user_id")
    ]
)
data class WorkoutGroup(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,
	@ColumnInfo(name = "creation_date")
    override var creationDate: LocalDateTime = dateTimeNow(),
    @ColumnInfo(name = "update_date")
    override var updateDate: LocalDateTime = dateTimeNow(),
    @ColumnInfo(name = "creation_user_id")
    override var creationUserId: String = "",
    @ColumnInfo(name = "update_user_id")
    override var updateUserId: String = "",
    var name: String? = null,
    @ColumnInfo(name = "workout_id")
    var workoutId: String? = null,
    @ColumnInfo(name = "day_week")
    var dayWeek: DayOfWeek? = null,
    var active: Boolean = true
): IntegratedModel()