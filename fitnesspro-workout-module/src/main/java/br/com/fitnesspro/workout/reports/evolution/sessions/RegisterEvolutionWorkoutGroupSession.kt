package br.com.fitnesspro.workout.reports.evolution.sessions

import android.content.Context
import android.text.TextPaint
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.local.data.access.dao.filters.RegisterEvolutionWorkoutReportFilter
import br.com.fitnesspro.pdf.generator.session.AbstractReportSession
import br.com.fitnesspro.pdf.generator.utils.Paints
import br.com.fitnesspro.tuple.reports.evolution.WorkoutGroupInfosTuple

class RegisterEvolutionWorkoutGroupSession(
    context: Context,
    private val workoutGroupInfosTuple: WorkoutGroupInfosTuple
) : AbstractReportSession<RegisterEvolutionWorkoutReportFilter>(context) {

    override suspend fun prepare(filter: RegisterEvolutionWorkoutReportFilter) {
        super.prepare(filter)

        this.title = "${workoutGroupInfosTuple.name} - ${workoutGroupInfosTuple.dayWeek.getFirstPartFullDisplayName()}"
    }

    override fun getTitlePaint(): TextPaint {
        return Paints.subtitlePaintMoreFocus
    }
}