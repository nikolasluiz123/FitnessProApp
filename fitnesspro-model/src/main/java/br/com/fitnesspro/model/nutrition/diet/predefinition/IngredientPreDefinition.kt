package br.com.fitnesspro.model.nutrition.diet.predefinition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumUnity
import java.time.LocalDateTime
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
    ],
    indices = [
        Index("meal_option_pre_definition_id")
    ]
)
data class IngredientPreDefinition(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,
    var name: String? = null,
    var quantity: Double? = null,
    var unit: EnumUnity? = null,
    @ColumnInfo(name = "meal_option_pre_definition_id")
    var mealOptionPreDefinitionId: String? = null,
    var active: Boolean = true
): IntegratedModel()