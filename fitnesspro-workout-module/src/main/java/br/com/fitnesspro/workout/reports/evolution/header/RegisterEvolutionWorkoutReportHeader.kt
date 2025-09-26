package br.com.fitnesspro.workout.reports.evolution.header

import android.content.Context
import br.com.fitnesspro.local.data.access.dao.filters.RegisterEvolutionWorkoutReportFilter
import br.com.fitnesspro.pdf.generator.header.AbstractReportHeader
import br.com.fitnesspro.workout.R

class RegisterEvolutionWorkoutReportHeader(context: Context) : AbstractReportHeader<RegisterEvolutionWorkoutReportFilter>(context) {

    override suspend fun prepare(filter: RegisterEvolutionWorkoutReportFilter) {
        super.prepare(filter)
        this.title = context.getString(R.string.report_training_title)
    }
}