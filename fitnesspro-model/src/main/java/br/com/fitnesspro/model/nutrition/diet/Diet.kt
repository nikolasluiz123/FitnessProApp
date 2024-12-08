package br.com.fitnesspro.model.nutrition.diet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.general.Person
import java.util.UUID

@Entity(
    tableName = "diet",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["nutritionist_person_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["academy_member_person_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Diet(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "nutritionist_person_id")
    var nutritionistPersonId: String? = null,
    @ColumnInfo(name = "academy_member_person_id")
    var academyMemberPersonId: String? = null,
    var active: Boolean = true
): BaseModel()