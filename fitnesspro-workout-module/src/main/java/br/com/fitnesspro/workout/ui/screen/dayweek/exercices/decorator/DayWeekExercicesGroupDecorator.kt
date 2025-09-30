package br.com.fitnesspro.workout.ui.screen.dayweek.exercices.decorator

import br.com.android.ui.compose.components.list.grouped.IBasicGroup
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.decorator.WorkoutGroupDecorator

data class DayWeekExercicesGroupDecorator(
    override val id: String,
    override val label: String,
    override val items: List<WorkoutGroupDecorator>
): IBasicGroup<WorkoutGroupDecorator>
