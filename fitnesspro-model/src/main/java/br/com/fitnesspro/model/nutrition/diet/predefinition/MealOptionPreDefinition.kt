package br.com.fitnesspro.model.nutrition.diet.predefinition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "meal_option_pre_definition")
data class MealOptionPreDefinition(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,
    var name: String? = null,
    var description: String? = null,
    var active: Boolean = true
): IntegratedModel()