package br.com.fitnesspro.model.nutrition.diet.predefinition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.interfaces.sync.IntegratedModel
import java.util.UUID

@Entity(
    tableName = "meal_option_pre_definition",
)
data class MealOptionPreDefinition(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    var name: String? = null,
    var description: String? = null,
    var active: Boolean = true
): IntegratedModel