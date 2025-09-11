package br.com.fitnesspro.workout.ui.screen.evolution.callbacks

import br.com.fitnesspro.workout.ui.navigation.ExecutionEvolutionHistoryScreenArgs
import br.com.fitnesspro.workout.ui.navigation.RegisterEvolutionScreenArgs

fun interface OnNavigateToExecutionEvolutionHistory {
    fun onNavigate(args: ExecutionEvolutionHistoryScreenArgs)
}