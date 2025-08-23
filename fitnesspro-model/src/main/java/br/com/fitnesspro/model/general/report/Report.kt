package br.com.fitnesspro.model.general.report

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.FileModel
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.base.StorageModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "report")
data class Report(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    @ColumnInfo(name = "storage_transmission_state", defaultValue = "PENDING")
    override var storageTransmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    @ColumnInfo(name = "storage_transmission_date")
    override var storageTransmissionDate: LocalDateTime? = null,
    @ColumnInfo(name = "file_path")
    override var filePath: String? = null,
    @ColumnInfo(name = "kb_size")
    override var kbSize: Long? = null,
    @ColumnInfo(name = "storage_url", typeAffinity = ColumnInfo.TEXT)
    override var storageUrl: String? = null,
    var name: String? = null,
    var extension: String? = null,
    var date: LocalDateTime = LocalDateTime.now(),
    var active: Boolean = true
): IntegratedModel, StorageModel, FileModel