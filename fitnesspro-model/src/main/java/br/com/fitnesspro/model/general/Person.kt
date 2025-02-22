package br.com.fitnesspro.model.general

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import java.time.LocalDate
import java.time.LocalDateTime
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
    ],
    indices = [
        Index("user_id")
    ]
)
data class Person(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,
    var name: String? = null,
    @ColumnInfo(name = "birth_date")
    var birthDate: LocalDate? = null,
    var phone: String? = null,
    @ColumnInfo(name = "user_id")
    var userId: String? = null,
    var active: Boolean = true
): IntegratedModel()