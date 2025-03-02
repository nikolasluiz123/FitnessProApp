package br.com.fitnesspro.model.nutrition.diet.predefinition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
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
        ),
    ]
)
data class IngredientPreDefinition(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    var name: String? = null,
    var quantity: Double? = null,
    var unit: EnumUnity? = null,
    @ColumnInfo(name = "meal_option_pre_definition_id", index = true)
    var mealOptionPreDefinitionId: String? = null,
    var active: Boolean = true
): IntegratedModel