package br.com.fitnesspro.model.nutrition.diet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "meal_option")
data class MealOption(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,
    var name: String? = null,
    var description: String? = null,
    @ColumnInfo(name = "meal_id")
    var mealId: String? = null,
    var active: Boolean = true
): IntegratedModel()