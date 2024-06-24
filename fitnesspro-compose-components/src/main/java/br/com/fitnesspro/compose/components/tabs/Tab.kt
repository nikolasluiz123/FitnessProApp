package br.com.fitnesspro.compose.components.tabs

/**
 * Classe para representar uma Tab no [FitnessProTabRow] e [FitnessProHorizontalPager]
 */
class Tab(
    val enum: IEnumTab,
    var selected: Boolean,
    var enabled: Boolean
)