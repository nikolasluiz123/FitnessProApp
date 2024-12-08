package br.com.fitnesspro.model.nutrition.diet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import java.time.LocalTime
import java.util.UUID

@Entity(
    tableName = "meal",
    foreignKeys = [
        ForeignKey(
            entity = DayWeekDiet::class,
            parentColumns = ["id"],
            childColumns = ["day_week_diet_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Meal(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    var time: LocalTime? = null,
    @ColumnInfo(name = "day_week_diet_id")
    var dayWeekDietId: String? = null,
    var active: Boolean = true
): BaseModel()