package br.com.fitnesspro.compose.components.fields.state

import br.com.fitnesspro.compose.components.tabs.Tab

data class TabState(
    val tabs: MutableList<Tab> = mutableListOf(),
    val onSelectTab: (Tab) -> Unit = { },
) {
    val selectedTab: Tab
        get() = tabs.first { it.selected }

    val tabsSize: Int
        get() = tabs.size
}