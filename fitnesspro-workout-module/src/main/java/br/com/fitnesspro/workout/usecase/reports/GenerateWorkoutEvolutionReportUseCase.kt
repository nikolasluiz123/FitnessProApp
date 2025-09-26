package br.com.fitnesspro.workout.usecase.reports

import android.content.Context
import br.com.fitnesspro.common.R
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
import br.com.fitnesspro.workout.repository.RegisterEvolutionWorkoutRepository
import br.com.fitnesspro.workout.ui.state.reports.NewRegisterEvolutionReportResult
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.time.ZoneId

class GenerateWorkoutEvolutionReportUseCase(
    private val context: Context,
    private val reportRepository: RegisterEvolutionWorkoutRepository
) {

    suspend operator fun invoke(reportResult: NewRegisterEvolutionReportResult): GenerateWorkoutEvolutionReportUseCaseResult {
        val validationResult = mutableListOf<FieldValidationError<EnumValidatedRegisterEvolutionReportFields>>()
        validateName(reportResult)?.let(validationResult::add)

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
            workoutId = reportResult.workoutId!!
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
        )

        reportRepository.saveRegisterEvolutionReport(toReport, toWorkoutReport)
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
}

data class GenerateWorkoutEvolutionReportUseCaseResult(
    val filePath: String?,
    val validations: List<FieldValidationError<EnumValidatedRegisterEvolutionReportFields>>
)