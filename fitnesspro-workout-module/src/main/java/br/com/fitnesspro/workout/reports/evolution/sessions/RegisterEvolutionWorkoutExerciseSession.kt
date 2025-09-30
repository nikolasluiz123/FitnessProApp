package br.com.fitnesspro.workout.reports.evolution.sessions

import android.content.Context
import android.text.TextPaint
import br.com.android.pdf.generator.components.IReportComponent
import br.com.android.pdf.generator.components.layout.LayoutGridComponent
import br.com.android.pdf.generator.components.table.TableComponent
import br.com.android.pdf.generator.components.table.layout.ColumnLayout
import br.com.android.pdf.generator.session.AbstractReportSession
import br.com.android.pdf.generator.utils.Paints
import br.com.core.android.utils.extensions.toReadableDuration
import br.com.core.utils.enums.EnumDateTimePatterns
import br.com.core.utils.extensions.format
import br.com.core.utils.extensions.formatToDecimal
import br.com.fitnesspro.local.data.access.dao.filters.RegisterEvolutionWorkoutReportFilter
import br.com.fitnesspro.tuple.reports.evolution.ExecutionInfosTuple
import br.com.fitnesspro.tuple.reports.evolution.ExerciseInfosTuple
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.injection.IWorkoutReportsEntryPoint
import dagger.hilt.android.EntryPointAccessors

class RegisterEvolutionWorkoutExerciseSession(
    context: Context,
    private val exerciseInfosTuple: ExerciseInfosTuple,
) : AbstractReportSession<RegisterEvolutionWorkoutReportFilter>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IWorkoutReportsEntryPoint::class.java)

    private lateinit var executionData: List<ExecutionInfosTuple>

    override suspend fun prepare(filter: RegisterEvolutionWorkoutReportFilter) {
        super.prepare(filter)
        this.title = exerciseInfosTuple.name

        executionData = entryPoint.getRegisterEvolutionWorkoutRepository().getExecutionInfosTuple(exerciseInfosTuple.id, filter)

        val sessionComponents = mutableListOf<IReportComponent<RegisterEvolutionWorkoutReportFilter>>(
            LayoutGridComponent(
                columnCount = 4,
                items = listOfNotNull(
                    getSetsPair(),
                    getRepetitionsPair(),
                    getRestPair(),
                    getDurationPair()
                )
            ),
            TableComponent(
                columnLayouts = listOfNotNull(
                    getStartColumn(),
                    getEndColumn(),
                    getSetColumn(),
                    getWeightColumn(),
                    getRepetitionsColumn(),
                    getDurationColumn()
                ),
                rows = executionData.map {
                    listOfNotNull(
                        it.executionStartTime.format(EnumDateTimePatterns.DATE_TIME_SHORT),
                        it.executionEndTime?.format(EnumDateTimePatterns.DATE_TIME_SHORT),
                        it.actualSet.toString(),
                        it.weight?.formatToDecimal(),
                        it.repetitions?.toString(),
                        it.duration?.toReadableDuration(context)
                    )
                }
            )
        )
        this.components = sessionComponents
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

    private fun getDurationPair(): Pair<String, String>? {
        return exerciseInfosTuple.duration?.let {
            context.getString(R.string.duration_label) to it.toReadableDuration(context)
        }
    }

    private fun getRestPair(): Pair<String, String>? {
        return exerciseInfosTuple.rest?.let {
            context.getString(R.string.rest_label) to it.toReadableDuration(context)
        }
    }

    private fun getSetsPair(): Pair<String, String>? {
        return exerciseInfosTuple.sets?.let {
            context.getString(R.string.sets_label) to it.toString()
        }
    }

    private fun getRepetitionsPair(): Pair<String, String>? {
        return exerciseInfosTuple.repetitions?.let {
            context.getString(R.string.repetitions_label) to it.toString()
        }
    }

    override fun getTitlePaint(): TextPaint {
        return Paints.subtitlePaintGrey
    }
}