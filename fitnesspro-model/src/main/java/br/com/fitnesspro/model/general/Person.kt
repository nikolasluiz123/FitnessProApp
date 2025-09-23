package br.com.fitnesspro.model.general

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "person",
)
data class Person(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    var name: String? = null,
    @ColumnInfo(name = "birth_date")
    var birthDate: LocalDate? = null,
    var phone: String? = null,
    @ColumnInfo(name = "user_id", index = true)
    var userId: String? = null,
    var active: Boolean = true,
): IntegratedModel