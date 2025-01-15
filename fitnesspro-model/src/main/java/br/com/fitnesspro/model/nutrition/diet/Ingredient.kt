package br.com.fitnesspro.model.nutrition.diet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.enums.EnumUnity
import java.util.UUID

@Entity(
    tableName = "ingredient",
    foreignKeys = [
        ForeignKey(
            entity = MealOption::class,
            parentColumns = ["id"],
            childColumns = ["meal_option_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("meal_option_id")
    ]
)
data class Ingredient(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    var quantity: Double? = null,
    var unit: EnumUnity? = null,
    @ColumnInfo(name = "meal_option_id")
    var mealOptionId: String? = null,
    var active: Boolean = true
): BaseModel()