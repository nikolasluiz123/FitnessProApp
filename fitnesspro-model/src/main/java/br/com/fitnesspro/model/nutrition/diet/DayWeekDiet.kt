package br.com.fitnesspro.model.nutrition.diet

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
    tableName = "day_week_foods",
    foreignKeys = [
        ForeignKey(
            entity = Diet::class,
            parentColumns = ["id"],
            childColumns = ["diet_id"],
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
        Index("diet_id"),
        Index("creation_user_id"),
        Index("update_user_id")
    ]
)
data class DayWeekDiet(
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
    @ColumnInfo(name = "day_week")
    var dayWeek: DayOfWeek? = null,
    @ColumnInfo(name = "diet_id")
    var dietId: String? = null,
    var active: Boolean = true
): IntegratedModel()