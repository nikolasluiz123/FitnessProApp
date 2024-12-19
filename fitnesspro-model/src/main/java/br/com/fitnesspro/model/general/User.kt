package br.com.fitnesspro.model.general

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.enums.EnumUserType
import java.util.UUID

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    var email: String? = null,
    var password: String? = null,
    var type: EnumUserType? = null,
    var active: Boolean = true,
    var authenticated: Boolean = false,
): BaseModel()