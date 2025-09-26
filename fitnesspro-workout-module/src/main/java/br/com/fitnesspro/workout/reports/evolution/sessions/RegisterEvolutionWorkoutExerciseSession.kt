package br.com.fitnesspro.workout.reports.evolution.sessions

import android.content.Context
import android.text.TextPaint
import br.com.fitnesspro.core.extensions.toReadableDuration
import br.com.fitnesspro.local.data.access.dao.filters.RegisterEvolutionWorkoutReportFilter
import br.com.fitnesspro.pdf.generator.components.layout.LayoutGridComponent
import br.com.fitnesspro.pdf.generator.components.table.TableComponent
import br.com.fitnesspro.pdf.generator.components.table.layout.ColumnLayout
import br.com.fitnesspro.pdf.generator.session.AbstractReportSession
import br.com.fitnesspro.pdf.generator.utils.Paints
import br.com.fitnesspro.tuple.reports.evolution.ExecutionInfosTuple
import br.com.fitnesspro.tuple.reports.evolution.ExerciseInfosTuple
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.injection.IWorkoutReportsEntryPoint
import dagger.hilt.android.EntryPointAccessors

class RegisterEvolutionWorkoutExerciseSession(
    context: Context,
    private val exerciseInfosTuple: ExerciseInfosTuple
) : AbstractReportSession<RegisterEvolutionWorkoutReportFilter>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IWorkoutReportsEntryPoint::class.java)

    private lateinit var executionData: List<ExecutionInfosTuple>

    override suspend fun prepare(filter: RegisterEvolutionWorkoutReportFilter) {
        super.prepare(filter)
        this.title = exerciseInfosTuple.name

        executionData = entryPoint.getRegisterEvolutionWorkoutRepository().getExecutionInfosTuple(filter)

        this.components = listOfNotNull(
            LayoutGridComponent(
                columnCount = 3,
                items = listOfNotNull(
                    getRepetitionsPair(),
                    getSetsPair(),
                    getRestPair(),
                    getDurationPair()
                )
            ),
            TableComponent(
                columnLayouts = listOfNotNull(
                    getStartColumn(),
                    getSetColumn(),
                    getWeightColumn(),
                    getRepetitionsColumn(),
                    getEndColumn(),
                    getDurationColumn()
                ),
                rows = executionData.map {
                    listOfNotNull(
                        it.executionStartTime.toString(),
                        it.executionEndTime?.toString(),
                        it.actualSet.toString(),
                        it.weight?.toString(),
                        it.repetitions?.toString(),
                        it.duration?.toReadableDuration(context)
                    )
                }
            )
        )
    }

    private fun getDurationColumn(): ColumnLayout? =
        if (executionData.any { it.duration != null }) ColumnLayout(
            label = context.getString(R.string.duration_column_label),
            widthPercent = 0.2f
        ) else null

    private fun getRepetitionsColumn(): ColumnLayout? =
        if (executionData.any { it.repetitions != null }) ColumnLayout(
            label = context.getString(R.string.repetitions_column_label),
            widthPercent = 0.15f
        ) else null

    private fun getWeightColumn(): ColumnLayout? = if (executionData.any { it.weight != null }) ColumnLayout(
        label = context.getString(R.string.weight_column_label),
        widthPercent = 0.15f
    ) else null

    private fun getSetColumn(): ColumnLayout? = if (executionData.any { it.actualSet != 0 }) ColumnLayout(
        label = context.getString(R.string.set_column_label),
        widthPercent = 0.1f
    ) else null

    private fun getEndColumn(): ColumnLayout = ColumnLayout(
        label = context.getString(R.string.end_time_column_label),
        widthPercent = 0.2f
    )

    private fun getStartColumn(): ColumnLayout = ColumnLayout(
        label = context.getString(R.string.start_time_column_label),
        widthPercent = 0.2f
    )

    private fun getDurationPair(): Pair<String, String>? = exerciseInfosTuple.duration?.let {
        context.getString(R.string.duration_label) to it.toReadableDuration(context)
    }

    private fun getRestPair(): Pair<String, String>? = exerciseInfosTuple.rest?.let {
        context.getString(R.string.rest_label) to it.toReadableDuration(context)
    }

    private fun getSetsPair(): Pair<String, String>? = exerciseInfosTuple.sets?.let {
        context.getString(R.string.sets_label) to it.toString()
    }

    private fun getRepetitionsPair(): Pair<String, String>? = exerciseInfosTuple.repetitions?.let {
        context.getString(R.string.repetitions_label) to it.toString()
    }

    override fun getTitlePaint(): TextPaint {
        return Paints.subtitlePaintLessFocus
    }
}