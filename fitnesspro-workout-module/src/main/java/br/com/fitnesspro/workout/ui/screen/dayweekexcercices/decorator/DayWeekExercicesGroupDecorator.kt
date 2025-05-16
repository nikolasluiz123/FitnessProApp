package br.com.fitnesspro.workout.ui.screen.dayweekexcercices.decorator

import br.com.fitnesspro.compose.components.list.grouped.IBasicGroup
import br.com.fitnesspro.workout.ui.screen.dayweekworkout.decorator.WorkoutGroupDecorator

data class DayWeekExercicesGroupDecorator(
    override val id: String,
    override val label: String,
    override val items: List<WorkoutGroupDecorator>
): IBasicGroup<WorkoutGroupDecorator>
