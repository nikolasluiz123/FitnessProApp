package br.com.fitnesspro.model.nutrition.diet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.interfaces.sync.IntegratedModel
import java.util.UUID

@Entity(
    tableName = "diet",
)
data class Diet(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    @ColumnInfo(name = "nutritionist_person_id", index = true)
    var nutritionistPersonId: String? = null,
    @ColumnInfo(name = "academy_member_person_id", index = true)
    var academyMemberPersonId: String? = null,
    var active: Boolean = true
): IntegratedModel