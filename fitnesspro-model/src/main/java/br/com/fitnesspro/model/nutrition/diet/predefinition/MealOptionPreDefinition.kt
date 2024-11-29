package br.com.fitnesspro.model.nutrition.diet.predefinition

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "meal_option_pre_definition")
data class MealOptionPreDefinition(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    var description: String? = null,
    var active: Boolean = true
)