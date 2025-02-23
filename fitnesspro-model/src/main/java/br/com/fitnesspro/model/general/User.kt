package br.com.fitnesspro.model.general

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumUserType
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "user",
    foreignKeys = [
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
        Index("creation_user_id"),
        Index("update_user_id")
    ]
)
data class User(
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
    var email: String? = null,
    var password: String? = null,
    var type: EnumUserType? = null,
    var active: Boolean = true,
    @ColumnInfo(defaultValue = "0")
    var authenticated: Boolean = false,
    @ColumnInfo(name = "service_token")
    var serviceToken: String? = null
): IntegratedModel()