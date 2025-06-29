package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "video",
)
data class Video(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    var extension: String? = null,
    @ColumnInfo(name = "file_path")
    var filePath: String? = null,
    var date: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "kb_size")
    var kbSize: Long? = null,
    var seconds: Long? = null,
    var width: Int? = null,
    var height: Int? = null
) : IntegratedModel