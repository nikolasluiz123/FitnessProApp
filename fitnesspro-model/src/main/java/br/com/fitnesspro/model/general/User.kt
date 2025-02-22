package br.com.fitnesspro.model.general

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumUserType
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,
    var email: String? = null,
    var password: String? = null,
    var type: EnumUserType? = null,
    var active: Boolean = true,
    @ColumnInfo(defaultValue = "0")
    var authenticated: Boolean = false,
    @ColumnInfo(name = "service_token")
    var serviceToken: String? = null
): IntegratedModel()