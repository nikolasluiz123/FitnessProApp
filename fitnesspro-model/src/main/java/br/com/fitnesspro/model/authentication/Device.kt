package br.com.fitnesspro.model.authentication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import java.util.UUID

@Entity(
    tableName = "device",
    indices = [
        Index("application_id")
    ]
)
data class Device(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),

    var model: String? = null,

    var brand: String? = null,

    @ColumnInfo(name = "android_version")
    var androidVersion: String? = null,

    @ColumnInfo(name = "application_id")
    var applicationId: String? = null,

    var active: Boolean = true,

    @ColumnInfo(name = "person_id", index = true)
    var personId: String? = null,

    @ColumnInfo(name = "firebase_messaging_token")
    var firebaseMessagingToken: String? = null,

    @ColumnInfo(name = "zone_id")
    var zoneId: String? = null
) : BaseModel