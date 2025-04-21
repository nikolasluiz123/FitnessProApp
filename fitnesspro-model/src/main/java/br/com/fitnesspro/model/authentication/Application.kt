package br.com.fitnesspro.model.authentication

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import java.util.UUID

@Entity(tableName = "application")
data class Application(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    var active: Boolean = true,
) : BaseModel