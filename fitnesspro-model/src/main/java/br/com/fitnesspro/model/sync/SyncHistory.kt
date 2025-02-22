package br.com.fitnesspro.model.sync

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "sync_history")
data class SyncHistory(
    @PrimaryKey
    var module: EnumSyncModule,

    @ColumnInfo(name = "last_sync_date")
    var lastSyncDate: LocalDateTime? = null
)