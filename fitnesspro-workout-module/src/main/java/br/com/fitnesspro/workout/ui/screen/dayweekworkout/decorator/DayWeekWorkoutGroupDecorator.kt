package br.com.fitnesspro.workout.ui.screen.dayweekworkout.decorator

import br.com.fitnesspro.compose.components.list.grouped.IBasicGroup

data class DayWeekWorkoutGroupDecorator(
    override val id: String,
    override val label: String,
    override val items: List<DayWeekWorkoutItemDecorator>
): IBasicGroup<DayWeekWorkoutItemDecorator>