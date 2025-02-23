package br.com.fitnesspro.model.nutrition.diet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import java.time.LocalDateTime
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
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["creation_user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["update_user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("nutritionist_person_id"),
        Index("academy_member_person_id"),
        Index("creation_user_id"),
        Index("update_user_id")
    ]
)
data class Diet(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,
	@ColumnInfo(name = "creation_date")
    override var creationDate: LocalDateTime = dateTimeNow(),
    @ColumnInfo(name = "update_date")
    override var updateDate: LocalDateTime = dateTimeNow(),
    @ColumnInfo(name = "creation_user_id")
    override var creationUserId: String = "",
    @ColumnInfo(name = "update_user_id")
    override var updateUserId: String = "",
    @ColumnInfo(name = "nutritionist_person_id")
    var nutritionistPersonId: String? = null,
    @ColumnInfo(name = "academy_member_person_id")
    var academyMemberPersonId: String? = null,
    var active: Boolean = true
): IntegratedModel()