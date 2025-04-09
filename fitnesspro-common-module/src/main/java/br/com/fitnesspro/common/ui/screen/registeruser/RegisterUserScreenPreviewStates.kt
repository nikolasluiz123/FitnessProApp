package br.com.fitnesspro.common.ui.screen.registeruser

import br.com.fitnesspro.common.ui.bottomsheet.registeruser.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumTabsRegisterUserScreen
import br.com.fitnesspro.common.ui.state.RegisterUserUIState
import br.com.fitnesspro.compose.components.fields.state.TabState
import br.com.fitnesspro.compose.components.tabs.Tab

internal val registerUserServiceState = RegisterUserUIState(
    isRegisterServiceAuth = true
)

internal val registerUserWithFoneState = RegisterUserUIState(
    isVisibleFieldPhone = true
)

internal val registerUserSelectedTabAcademyState = RegisterUserUIState(
    title = "Título",
    subtitle = "Subtítulo",
    context = EnumOptionsBottomSheetRegisterUser.ACADEMY_MEMBER,
    tabState = TabState(
        tabs = mutableListOf(
            Tab(
                enum = EnumTabsRegisterUserScreen.GENERAL,
                selected = false,
                enabled = true
            ),
            Tab(
                enum = EnumTabsRegisterUserScreen.ACADEMY,
                selected = true,
                enabled = true
            )
        )
    )
)

internal val registerUserSelectedTabGeneralState = RegisterUserUIState(
    title = "Título",
    subtitle = "Subtítulo",
    context = EnumOptionsBottomSheetRegisterUser.ACADEMY_MEMBER,
    tabState = TabState(
        tabs = mutableListOf(
            Tab(
                enum = EnumTabsRegisterUserScreen.GENERAL,
                selected = true,
                enabled = true
            ),
            Tab(
                enum = EnumTabsRegisterUserScreen.ACADEMY,
                selected = false,
                enabled = false
            )
        )
    )
)