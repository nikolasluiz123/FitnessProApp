package br.com.fitnesspro.scheduler.usecase.report

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.NAME
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.local.data.access.dao.filters.SchedulerReportFilter
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.pdf.generator.enums.EnumPageSize
import br.com.fitnesspro.pdf.generator.report.PDFReportGenerator
import br.com.fitnesspro.scheduler.reports.schedules.report.SchedulerPDFReport
import br.com.fitnesspro.scheduler.repository.SchedulerReportRepository
import br.com.fitnesspro.scheduler.ui.state.NewSchedulerReportResult
import br.com.fitnesspro.scheduler.usecase.report.enums.EnumValidatedSchedulerReportFields
import br.com.fitnesspro.to.TOReport
import br.com.fitnesspro.to.TOSchedulerReport
import java.io.File
import java.time.ZoneId

class GenerateSchedulerReportUseCase(
    private val context: Context,
    private val schedulerReportRepository: SchedulerReportRepository,
    private val personRepository: PersonRepository
) {

    suspend operator fun invoke(reportResult: NewSchedulerReportResult): GenerateSchedulerReportUseCaseResult {
        val validationResult = mutableListOf<FieldValidationError<EnumValidatedSchedulerReportFields>>()
        validateName(reportResult)?.let(validationResult::add)

        var file: File? = null

        return if (validationResult.isEmpty()) {
            try {
                val filter = getReportFilter(reportResult)
                file = generateReport(filter)
                saveGeneratedReport(reportResult, file, filter)

                GenerateSchedulerReportUseCaseResult(
                    filePath = file.absolutePath,
                    validations = validationResult
                )
            } catch (e: Exception) {
                file?.delete()
                throw e
            }
        } else {
            GenerateSchedulerReportUseCaseResult(
                filePath = null,
                validations = validationResult
            )
        }
    }

    private suspend fun getReportFilter(reportResult: NewSchedulerReportResult): SchedulerReportFilter {
        val authenticatedPersonId = personRepository.getAuthenticatedTOPerson()?.id!!

        return SchedulerReportFilter(
            personId = authenticatedPersonId,
            dateStart = reportResult.dateStart,
            dateEnd = reportResult.dateEnd
        )
    }

    private suspend fun generateReport(filter: SchedulerReportFilter): File {
        val report = SchedulerPDFReport(context, filter)
        val reportGenerator = PDFReportGenerator<SchedulerReportFilter>(context, report)

        return reportGenerator.generatePdfFile(pageSize = EnumPageSize.A4)
    }

    private suspend fun saveGeneratedReport(
        reportResult: NewSchedulerReportResult,
        file: File,
        filter: SchedulerReportFilter
    ) {
        val toReport = TOReport(
            name = reportResult.reportName!!,
            extension = file.extension,
            filePath = file.absolutePath,
            date = dateTimeNow(ZoneId.systemDefault()),
            kbSize = FileUtils.getFileSizeInKB(file)
        )

        val toSchedulerReport = TOSchedulerReport(
            personId = filter.personId,
            context = EnumReportContext.SCHEDULERS_REPORT
        )

        schedulerReportRepository.saveSchedulerReport(toReport, toSchedulerReport)
    }

    private fun validateName(result: NewSchedulerReportResult): FieldValidationError<EnumValidatedSchedulerReportFields>? {
        val name = result.reportName?.trim()

        val validationPair = when {
            name.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedSchedulerReportFields.NAME.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedSchedulerReportFields.NAME,
                    message = message,
                )
            }

            name.length > NAME.maxLength -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(EnumValidatedSchedulerReportFields.NAME.labelResId),
                    EnumValidatedSchedulerReportFields.NAME.maxLength
                )

                FieldValidationError(
                    field = EnumValidatedSchedulerReportFields.NAME,
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

data class GenerateSchedulerReportUseCaseResult(
    val filePath: String?,
    val validations: List<FieldValidationError<EnumValidatedSchedulerReportFields>>
)