package br.com.fitnesspro.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

@Entity(tableName = "users")
class User(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    @ColumnInfo(name = "birth_date")
    var birthDate: LocalDate? = null,
    var phone: String? = null,
)

enum class EnumUserValidatedFields {
    NAME, EMAIL, PASSWORD, BIRTH_DATE
}