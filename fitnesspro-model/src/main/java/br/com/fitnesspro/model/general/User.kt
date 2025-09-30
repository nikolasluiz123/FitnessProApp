package br.com.fitnesspro.model.general

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.android.firebase.toolkit.authentication.interfaces.IFirebaseUser
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.interfaces.sync.IntegratedModel
import br.com.fitnesspro.model.enums.EnumUserType
import java.util.UUID

@Entity(
    tableName = "user",
)
data class User(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    override var email: String? = null,
    override var password: String? = null,
    var type: EnumUserType? = null,
    var active: Boolean = true,
): IntegratedModel, IFirebaseUser