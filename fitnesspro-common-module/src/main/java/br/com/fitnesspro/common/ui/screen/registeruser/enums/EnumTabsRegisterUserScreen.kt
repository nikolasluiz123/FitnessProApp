package br.com.fitnesspro.common.ui.screen.registeruser.enums

import br.com.fitnesspro.common.R
import br.com.fitnesspro.compose.components.tabs.IEnumTab

enum class EnumTabsRegisterUserScreen(
    override val index: Int,
    override val labelResId: Int
): IEnumTab {
    GENERAL(index = 0, labelResId = R.string.register_user_screen_label_tab_general),
    ACADEMY(index = 1, labelResId = R.string.register_user_screen_label_tab_gym)
}