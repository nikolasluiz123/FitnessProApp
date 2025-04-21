package br.com.fitnesspro.model.authentication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import java.util.UUID

@Entity(
    tableName = "device",
    foreignKeys = [
        ForeignKey(
            entity = Application::class,
            parentColumns = ["id"],
            childColumns = ["application_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
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
) : BaseModel