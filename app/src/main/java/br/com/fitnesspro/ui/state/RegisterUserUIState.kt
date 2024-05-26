package br.com.fitnesspro.ui.state

import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.model.User
import br.com.fitnesspro.ui.bottomsheet.EnumOptionsBottomSheetRegisterUser

data class RegisterUserUIState(
    val title: String? = null,
    val subtitle: String? = null,
    val context: EnumOptionsBottomSheetRegisterUser? = null,
    val user: User? = null,
    val tabs: MutableList<Tab> = mutableListOf(),
    val name: Field = Field(),
    val email: Field = Field(),
    val password: Field = Field(),
    val birthDate: Field = Field(),
    val phone: Field = Field(),
)