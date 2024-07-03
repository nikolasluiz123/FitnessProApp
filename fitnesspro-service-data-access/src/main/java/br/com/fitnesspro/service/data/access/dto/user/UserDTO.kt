package br.com.fitnesspro.service.data.access.dto.user

import br.com.fitnesspro.model.enums.EnumUserProfile
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class UserDTO(
    var id: Long? = null,
    var password: String,
    @SerializedName("last_login")
    var lastLogin: LocalDateTime? = null,
    @SerializedName("is_superuser")
    var isSuperUser: Boolean = false,
    var username: String,
    @SerializedName("first_name")
    var firstName: String,
    @SerializedName("last_name")
    var lastName: String,
    var email: String,
    @SerializedName("user_profile")
    var profile: EnumUserProfile?,
    @SerializedName("is_staff")
    var isStaff: Boolean = false,
    @SerializedName("is_active")
    var isActive: Boolean = true,
    @SerializedName("date_joined")
    var dateJoined: LocalDateTime = LocalDateTime.now(),
    var groups: List<String> = emptyList(),
    @SerializedName("user_permissions")
    var userPermissions: List<String> = emptyList(),
    @SerializedName("group_names")
    var groupNames: List<String> = emptyList()
)


