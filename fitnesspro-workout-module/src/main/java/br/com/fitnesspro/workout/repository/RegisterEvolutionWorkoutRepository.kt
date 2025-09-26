package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.local.data.access.dao.ExerciseExecutionDAO
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutReportDAO
import br.com.fitnesspro.local.data.access.dao.filters.RegisterEvolutionWorkoutReportFilter
import br.com.fitnesspro.mappers.getReport
import br.com.fitnesspro.mappers.getWorkoutReport
import br.com.fitnesspro.to.TOReport
import br.com.fitnesspro.to.TOWorkoutReport
import br.com.fitnesspro.tuple.reports.evolution.ExecutionInfosTuple
import br.com.fitnesspro.tuple.reports.evolution.ExerciseInfosTuple
import br.com.fitnesspro.tuple.reports.evolution.ResumeRegisterEvolutionWorkoutGroupTuple
import br.com.fitnesspro.tuple.reports.evolution.ResumeRegisterEvolutionWorkoutTuple
import br.com.fitnesspro.tuple.reports.evolution.WorkoutGroupInfosTuple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterEvolutionWorkoutRepository(
    context: Context,
    private val workoutDAO: WorkoutDAO,
    private val workoutGroupDAO: WorkoutGroupDAO,
    private val exerciseDAO: ExerciseDAO,
    private val exerciseExecutionDAO: ExerciseExecutionDAO,
    private val reportDAO: ReportDAO,
    private val workoutReportDAO: WorkoutReportDAO
) : FitnessProRepository(context) {

    suspend fun getResumeRegisterEvolutionWorkoutTuple(filter: RegisterEvolutionWorkoutReportFilter): ResumeRegisterEvolutionWorkoutTuple = withContext(Dispatchers.IO) {
        workoutDAO.getResumeRegisterEvolutionWorkoutTuple(filter)
    }

    suspend fun getResumeRegisterEvolutionWorkoutGroupTuple(filter: RegisterEvolutionWorkoutReportFilter): List<ResumeRegisterEvolutionWorkoutGroupTuple> = withContext(Dispatchers.IO) {
        workoutDAO.getResumeRegisterEvolutionWorkoutGroupTuple(filter)
    }

    suspend fun getWorkoutGroupInfosTuple(filter: RegisterEvolutionWorkoutReportFilter): List<WorkoutGroupInfosTuple> = withContext(Dispatchers.IO) {
        workoutGroupDAO.getWorkoutGroupInfosTuple(filter)
    }

    suspend fun getExerciseInfosTuple(workoutGroupId: String): List<ExerciseInfosTuple> = withContext(Dispatchers.IO) {
        exerciseDAO.getExerciseInfosTuple(workoutGroupId)
    }

    suspend fun getExecutionInfosTuple(
        exerciseId: String,
        filter: RegisterEvolutionWorkoutReportFilter
    ): List<ExecutionInfosTuple> = withContext(Dispatchers.IO) {
        exerciseExecutionDAO.getExecutionInfosTuple(exerciseId, filter)
    }

    suspend fun saveRegisterEvolutionReport(toReport: TOReport, toWorkoutReport: TOWorkoutReport) {
        runInTransaction {
            val report = toReport.getReport()
            val workoutReport = toWorkoutReport.getWorkoutReport()
            workoutReport.reportId = report.id

            if (toReport.id == null) {
                reportDAO.insert(report)
                workoutReportDAO.insert(workoutReport)
            } else {
                reportDAO.update(report)
                workoutReportDAO.update(workoutReport)
            }

            toReport.id = report.id
            toWorkoutReport.id = workoutReport.id
        }
    }
}