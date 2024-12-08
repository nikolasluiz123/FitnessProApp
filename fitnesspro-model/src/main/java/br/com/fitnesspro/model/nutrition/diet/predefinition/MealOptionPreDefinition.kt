package br.com.fitnesspro.model.nutrition.diet.predefinition

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import java.util.UUID

@Entity(tableName = "meal_option_pre_definition")
data class MealOptionPreDefinition(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    var description: String? = null,
    var active: Boolean = true
): BaseModel()