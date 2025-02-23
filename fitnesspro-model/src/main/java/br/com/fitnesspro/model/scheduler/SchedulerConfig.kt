package br.com.fitnesspro.model.scheduler

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import java.time.LocalDateTime
import java.util.UUID


@Entity(
    tableName = "scheduler_config",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["person_id"],
            onDelete = ForeignKey.CASCADE,
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
        Index("person_id"),
        Index("creation_user_id"),
        Index("update_user_id")
    ]
)
data class SchedulerConfig(
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
    var alarm: Boolean = false,
    var notification: Boolean = false,
    @ColumnInfo(name = "min_schedule_density")
    var minScheduleDensity: Int = 1,
    @ColumnInfo(name = "max_schedule_density")
    var maxScheduleDensity: Int = 2,
    @ColumnInfo(name = "person_id")
    var personId: String? = null
): IntegratedModel()