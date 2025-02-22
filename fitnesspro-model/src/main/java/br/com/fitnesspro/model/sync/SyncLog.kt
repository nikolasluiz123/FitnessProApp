package br.com.fitnesspro.model.sync

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "sync_log")
data class SyncLog(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),

    var module: EnumSyncModule? = null,

    var status: EnumSyncStatus? = null,

    var type: EnumSyncType? = null,

    var description: String? = null,

    @ColumnInfo(name = "start_date")
    var startDate: LocalDateTime? = null,

    @ColumnInfo(name = "end_date")
    var endDate: LocalDateTime? = null,

    @ColumnInfo(name = "process_details", typeAffinity = ColumnInfo.TEXT)
    var processDetails: String? = null,
): BaseModel()