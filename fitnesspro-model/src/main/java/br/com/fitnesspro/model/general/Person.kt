package br.com.fitnesspro.model.general

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "person",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Person(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    @ColumnInfo(name = "birth_date")
    var birthDate: LocalDate? = null,
    var phone: String? = null,
    @ColumnInfo(name = "user_id")
    var userId: String? = null,
    var active: Boolean = true
): BaseModel()