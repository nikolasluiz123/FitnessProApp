package br.com.fitnesspro.model.nutrition.diet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.util.UUID

@Entity(
    tableName = "day_week_foods",
    foreignKeys = [
        ForeignKey(
            entity = Diet::class,
            parentColumns = ["id"],
            childColumns = ["diet_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DayWeekDiet(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "day_week")
    var dayWeek: DayOfWeek? = null,
    @ColumnInfo(name = "diet_id")
    var dietId: String? = null,
    var active: Boolean = true
)