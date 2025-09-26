package br.com.fitnesspro.workout.reports.evolution.sessions

import android.content.Context
import android.text.TextPaint
import br.com.fitnesspro.local.data.access.dao.filters.RegisterEvolutionWorkoutReportFilter
import br.com.fitnesspro.pdf.generator.components.layout.LayoutGridComponent
import br.com.fitnesspro.pdf.generator.components.table.TableComponent
import br.com.fitnesspro.pdf.generator.components.table.layout.ColumnLayout
import br.com.fitnesspro.pdf.generator.session.AbstractReportSession
import br.com.fitnesspro.pdf.generator.utils.Paints
import br.com.fitnesspro.tuple.reports.evolution.ResumeRegisterEvolutionWorkoutGroupTuple
import br.com.fitnesspro.tuple.reports.evolution.ResumeRegisterEvolutionWorkoutTuple
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.injection.IWorkoutReportsEntryPoint
import dagger.hilt.android.EntryPointAccessors

class RegisterEvolutionWorkoutResumeSession(context: Context) : AbstractReportSession<RegisterEvolutionWorkoutReportFilter>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IWorkoutReportsEntryPoint::class.java)
    private lateinit var resumeData: ResumeRegisterEvolutionWorkoutTuple
    private lateinit var groupData: List<ResumeRegisterEvolutionWorkoutGroupTuple>

    override suspend fun prepare(filter: RegisterEvolutionWorkoutReportFilter) {
        super.prepare(filter)
        title = context.getString(R.string.training_summary_session_title)
        resumeData = entryPoint.getRegisterEvolutionWorkoutRepository().getResumeRegisterEvolutionWorkoutTuple(filter)
        groupData = entryPoint.getRegisterEvolutionWorkoutRepository().getResumeRegisterEvolutionWorkoutGroupTuple(filter)

        this.components = listOf(
            LayoutGridComponent(
                columnCount = 3,
                items = listOf(
                    context.getString(R.string.start_date_label) to resumeData.dateStart.toString(),
                    context.getString(R.string.end_date_label) to resumeData.dateEnd.toString(),
                    context.getString(R.string.professional_label) to resumeData.professionalPersonName
                )
            ),
            TableComponent(
                columnLayouts = listOf(
                    ColumnLayout(
                        label = context.getString(R.string.day_of_week_column_label),
                        widthPercent = 0.5f
                    ),
                    ColumnLayout(
                        label = context.getString(R.string.name_column_label),
                        widthPercent = 0.5f
                    )
                ),
                rows = groupData.map {
                    listOf(
                        it.dayWeek.name,
                        it.name
                    )
                }
            )
        )
    }

    override fun getTitlePaint(): TextPaint {
        return Paints.subtitlePaintMoreFocus
    }
}