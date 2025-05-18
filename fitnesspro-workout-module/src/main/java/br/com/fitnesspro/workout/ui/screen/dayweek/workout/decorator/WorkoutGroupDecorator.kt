package br.com.fitnesspro.workout.ui.screen.dayweek.workout.decorator

import br.com.fitnesspro.compose.components.list.grouped.IBasicGroup
import br.com.fitnesspro.to.TOExercise

data class WorkoutGroupDecorator(
    override val id: String,
    override val label: String,
    override val items: List<TOExercise>
): IBasicGroup<TOExercise>