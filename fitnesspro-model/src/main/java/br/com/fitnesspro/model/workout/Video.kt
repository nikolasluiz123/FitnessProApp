package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.FileModel
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.base.StorageModel
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
    @ColumnInfo(name = "storage_transmission_state", defaultValue = "PENDING")
    override var storageTransmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    var extension: String? = null,
    @ColumnInfo(name = "file_path")
    override var filePath: String? = null,
    var date: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "kb_size")
    override var kbSize: Long? = null,
    var seconds: Long? = null,
    var width: Int? = null,
    var height: Int? = null,
    @ColumnInfo(name = "active", defaultValue = "true")
    var active: Boolean = true
) : IntegratedModel, StorageModel, FileModel