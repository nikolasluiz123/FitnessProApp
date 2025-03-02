package br.com.fitnesspro.model.general

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import java.util.UUID

@Entity(
    tableName = "academy",
)
data class Academy(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    var address: String? = null,
    var phone: String? = null,
    var active: Boolean = true,
) : BaseModel