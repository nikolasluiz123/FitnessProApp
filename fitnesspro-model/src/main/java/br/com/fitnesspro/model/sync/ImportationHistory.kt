package br.com.fitnesspro.model.sync

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.enums.EnumSyncModule
import java.time.LocalDateTime

@Entity(tableName = "importation_history")
data class ImportationHistory(
    @PrimaryKey
    var module: EnumSyncModule,

    @ColumnInfo(name = "date")
    var date: LocalDateTime? = null
)