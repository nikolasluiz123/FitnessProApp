package br.com.fitnesspro.model.nutrition.diet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import java.util.UUID

@Entity(tableName = "meal_option")
data class MealOption(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    var description: String? = null,
    @ColumnInfo(name = "meal_id")
    var mealId: String? = null,
    var active: Boolean = true
): BaseModel()