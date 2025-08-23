package br.com.fitnesspro.model.sync

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.enums.EnumImportationModule
import java.time.LocalDateTime

@Entity(tableName = "importation_history")
data class ImportationHistory(
    @PrimaryKey
    var module: EnumImportationModule,

    @ColumnInfo(name = "date")
    var date: LocalDateTime? = null
)