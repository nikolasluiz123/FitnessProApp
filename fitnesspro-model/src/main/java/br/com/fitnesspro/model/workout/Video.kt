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
    tableName = "video",
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
data class Video(
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
    var extension: String? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var data: ByteArray? = null,
    var date: LocalDateTime = LocalDateTime.now(),
) : IntegratedModel() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Video

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}