package br.com.fitnesspro.model.nutrition.diet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.interfaces.sync.IntegratedModel
import br.com.fitnesspro.model.enums.EnumUnity
import java.util.UUID

@Entity(
    tableName = "ingredient",
)
data class Ingredient(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    var name: String? = null,
    var quantity: Double? = null,
    var unit: EnumUnity? = null,
    @ColumnInfo(name = "meal_option_id", index = true)
    var mealOptionId: String? = null,
    var active: Boolean = true
): IntegratedModel