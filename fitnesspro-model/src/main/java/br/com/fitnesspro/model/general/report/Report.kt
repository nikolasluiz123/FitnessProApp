package br.com.fitnesspro.model.general.report

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "report")
data class Report(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    var name: String? = null,
    var extension: String? = null,
    @ColumnInfo(name = "file_path")
    var filePath: String? = null,
    var date: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "kb_size")
    var kbSize: Long? = null
): IntegratedModel