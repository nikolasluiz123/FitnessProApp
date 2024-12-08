package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "video")
data class Video(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    var extension: String? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var data: ByteArray? = null,
    var date: LocalDateTime = LocalDateTime.now(),
) : BaseModel() {

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