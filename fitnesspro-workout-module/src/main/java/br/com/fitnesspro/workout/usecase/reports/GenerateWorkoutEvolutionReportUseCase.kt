package br.com.fitnesspro.workout.usecase.reports

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.NAME
import br.com.fitnesspro.core.extensions.dataStore
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.extensions.setRunExportWorker
import br.com.fitnesspro.core.extensions.setRunImportWorker
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.local.data.access.dao.filters.RegisterEvolutionWorkoutReportFilter
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.pdf.generator.enums.EnumPageSize
import br.com.fitnesspro.pdf.generator.report.PDFReportGenerator
import br.com.fitnesspro.to.TOReport
import br.com.fitnesspro.to.TOWorkoutReport
import br.com.fitnesspro.workout.reports.evolution.report.RegisterEvolutionWorkoutPDFReport
import br.com.fitnesspro.workout.repository.ExerciseExecutionRepository
import br.com.fitnesspro.workout.repository.RegisterEvolutionWorkoutRepository
import br.com.fitnesspro.workout.ui.state.reports.NewRegisterEvolutionReportResult
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class GenerateWorkoutEvolutionReportUseCase(
    private val context: Context,
    private val reportRepository: RegisterEvolutionWorkoutRepository,
    private val exerciseExecutionRepository: ExerciseExecutionRepository,
    private val personRepository: PersonRepository
) {

    suspend operator fun invoke(reportResult: NewRegisterEvolutionReportResult): GenerateWorkoutEvolutionReportUseCaseResult {
        val validationResult = mutableListOf<FieldValidationError<EnumValidatedRegisterEvolutionReportFields>>()
        validateWorkout(reportResult)?.let(validationResult::add)
        validateName(reportResult)?.let(validationResult::add)
        validatePeriod(reportResult)?.let(validationResult::add)

        var file: File? = null

        return if (validationResult.isEmpty()) {
            try {
                blockWorkersRun()

                val filter = getReportFilter(reportResult)
                file = generateReport(filter)
                saveGeneratedReport(reportResult, file, filter)

                GenerateWorkoutEvolutionReportUseCaseResult(
                    filePath = file.absolutePath,
                    validations = validationResult
                )
            } catch (e: Exception) {
                file?.delete()
                throw e
            } finally {
                unblockWorkersRun()
            }
        } else {
            GenerateWorkoutEvolutionReportUseCaseResult(
                filePath = null,
                validations = validationResult
            )
        }
    }


    private suspend fun blockWorkersRun() {
        context.dataStore.setRunImportWorker(false)
        context.dataStore.setRunExportWorker(false)
    }

    private suspend fun unblockWorkersRun() {
        context.dataStore.setRunImportWorker(true)
        context.dataStore.setRunExportWorker(true)
    }

    private fun getReportFilter(reportResult: NewRegisterEvolutionReportResult): RegisterEvolutionWorkoutReportFilter {
        return RegisterEvolutionWorkoutReportFilter(
            workoutId = reportResult.workoutId!!,
            dateStart = reportResult.dateStart,
            dateEnd = reportResult.dateEnd
        )
    }

    private suspend fun generateReport(filter: RegisterEvolutionWorkoutReportFilter): File {
        val report = RegisterEvolutionWorkoutPDFReport(context, filter)
        val reportGenerator = PDFReportGenerator(context, report)

        return reportGenerator.generatePdfFile(pageSize = EnumPageSize.A4)
    }

    private suspend fun saveGeneratedReport(
        reportResult: NewRegisterEvolutionReportResult,
        file: File,
        filter: RegisterEvolutionWorkoutReportFilter
    ): Unit = withContext(IO) {
        val toReport = TOReport(
            name = reportResult.reportName!!,
            extension = file.extension,
            filePath = file.absolutePath,
            date = dateTimeNow(ZoneId.systemDefault()),
            kbSize = FileUtils.getFileSizeInKB(file)
        )

        val toWorkoutReport = TOWorkoutReport(
            context = EnumReportContext.WORKOUT_REGISTER_EVOLUTION,
            workoutId = filter.workoutId,
            personId = personRepository.getAuthenticatedTOPerson()?.id!!
        )

        reportRepository.saveRegisterEvolutionReport(toReport, toWorkoutReport)
    }

    private fun validateWorkout(result: NewRegisterEvolutionReportResult): FieldValidationError<EnumValidatedRegisterEvolutionReportFields>? {
        val validationPair = when {
            result.workoutId.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedRegisterEvolutionReportFields.WORKOUT.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedRegisterEvolutionReportFields.WORKOUT,
                    message = message,
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateName(result: NewRegisterEvolutionReportResult): FieldValidationError<EnumValidatedRegisterEvolutionReportFields>? {
        val name = result.reportName?.trim()

        val validationPair = when {
            name.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedRegisterEvolutionReportFields.NAME.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedRegisterEvolutionReportFields.NAME,
                    message = message,
                )
            }

            name.length > NAME.maxLength -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(EnumValidatedRegisterEvolutionReportFields.NAME.labelResId),
                    EnumValidatedRegisterEvolutionReportFields.NAME.maxLength
                )

                FieldValidationError(
                    field = EnumValidatedRegisterEvolutionReportFields.NAME,
                    message = message,
                )
            }

            else -> null
        }

        if (validationPair == null) {
            result.reportName = name
        }

        return validationPair
    }

    private suspend fun validatePeriod(result: NewRegisterEvolutionReportResult): FieldValidationError<EnumValidatedRegisterEvolutionReportFields>? {
        val period = exerciseExecutionRepository.getExecutionStartEnd(result.workoutId!!)
        if (period == null) return null

        val days = ChronoUnit.DAYS.between(period.executionStartTime, period.executionEndTime)

        return if (days > 90) {
            when {
                result.dateStart == null || result.dateEnd == null -> {
                    val message = context.getString(R.string.validation_msg_required_period_from_report)

                    FieldValidationError(
                        field = null,
                        message = message,
                    )
                }

                ChronoUnit.DAYS.between(result.dateStart, result.dateEnd) > 90 -> {
                    val message = context.getString(R.string.validation_msg_invalid_period_from_report)

                    FieldValidationError(
                        field = null,
                        message = message,
                    )
                }

                else -> null
            }
        } else {
            null
        }
    }
}

data class GenerateWorkoutEvolutionReportUseCaseResult(
    val filePath: String?,
    val validations: List<FieldValidationError<EnumValidatedRegisterEvolutionReportFields>>
)