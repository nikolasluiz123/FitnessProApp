package br.com.fitnesspro.ui.screen.home

import br.com.fitnesspro.ui.state.HomeUIState

internal val defaultHomeState = HomeUIState(
    title = "Membro",
    subtitle = "Nikolas Luiz Schmitt",
    isEnabledSchedulerButton = true,
    isEnabledWorkoutButton = true,
    isEnabledNutritionButton = true,
    isEnabledMoneyButton = true
)

internal val disabledHomeState = HomeUIState(
    title = "Membro",
    subtitle = "Nikolas Luiz Schmitt"
)