package br.com.fitnesspro.model.authentication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.enums.EnumTokenType
import br.com.fitnesspro.model.general.User
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "service_token",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = CASCADE,
        ),
        ForeignKey(
            entity = Application::class,
            parentColumns = ["id"],
            childColumns = ["application_id"],
            onDelete = CASCADE,
        ),
        ForeignKey(
            entity = Device::class,
            parentColumns = ["id"],
            childColumns = ["device_id"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["application_id"]),
        Index(value = ["device_id"]),
    ]
)
data class ServiceToken(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "jwt_token", typeAffinity = ColumnInfo.TEXT)
    val jwtToken: String? = null,

    val type: EnumTokenType? = null,

    @ColumnInfo(name = "creation_date")
    val creationDate: LocalDateTime? = null,

    @ColumnInfo(name = "expiration_date")
    var expirationDate: LocalDateTime? = null,

    @ColumnInfo(name = "user_id")
    var userId: String? = null,

    @ColumnInfo(name = "device_id")
    var deviceId: String? = null,

    @ColumnInfo(name = "application_id")
    var applicationId: String? = null,
) : BaseModel