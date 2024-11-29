package br.com.fitnesspro.model.nutrition.diet.predefinition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.enums.EnumUnity
import java.util.UUID

@Entity(
    tableName = "ingredient_pre_definition",
    foreignKeys = [
        ForeignKey(
            entity = MealOptionPreDefinition::class,
            parentColumns = ["id"],
            childColumns = ["meal_option_pre_definition_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IngredientPreDefinition(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    var quantity: Double? = null,
    var unit: EnumUnity? = null,
    @ColumnInfo(name = "meal_option_pre_definition_id")
    var mealOptionPreDefinitionId: String? = null,
    var active: Boolean = true
)