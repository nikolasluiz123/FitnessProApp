package br.com.fitnesspro.compose.components.tabs

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * Classe para representar uma Tab no [FitnessProTabRow] e [FitnessProHorizontalPager]
 */
class Tab(
    val enum: IEnumTab,
    var selected: MutableState<Boolean> = mutableStateOf(false),
    val isEnabled: () -> Boolean
)