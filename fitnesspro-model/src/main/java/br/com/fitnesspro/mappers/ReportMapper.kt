package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.model.general.report.SchedulerReport
import br.com.fitnesspro.shared.communication.dtos.general.ReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.SchedulerReportDTO
import br.com.fitnesspro.to.TOReport
import br.com.fitnesspro.to.TOSchedulerReport
import br.com.fitnesspro.shared.communication.enums.report.EnumReportContext as EnumReportContextService

fun TOReport.getReport(): Report {
    val model = Report(
        name = name,
        extension = extension,
        filePath = filePath,
        date = date!!,
        kbSize = kbSize,
    )

    id?.let { model.id = it }

    return model
}

fun TOSchedulerReport.getSchedulerReport(): SchedulerReport {
    val model = SchedulerReport(
        personId = personId,
        reportId = reportId,
        context = context
    )

    id?.let { model.id = it }

    return model
}

fun Report.getTOReport(): TOReport {
    return TOReport(
        id = id,
        name = name,
        extension = extension,
        filePath = filePath,
        date = date,
        kbSize = kbSize,
    )
}

fun SchedulerReport.getTOSchedulerReport(): TOSchedulerReport {
    return TOSchedulerReport(
        id = id,
        personId = personId,
        reportId = reportId,
        context = context
    )
}

fun ReportDTO.getReport(): Report {
    return Report(
        id = id!!,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        name = name,
        extension = extension,
        filePath = filePath,
        date = date!!,
        kbSize = kbSize,
    )
}

fun SchedulerReportDTO.getSchedulerReport(): SchedulerReport {
    return SchedulerReport(
        id = id!!,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        personId = personId,
        reportId = report?.id,
        context = getEnumReportContext(reportContext!!)
    )
}

fun Report.getReportDTO(): ReportDTO {
    return ReportDTO(
        id = id,
        name = name,
        extension = extension,
        filePath = filePath,
        date = date,
        kbSize = kbSize,
    )
}

fun SchedulerReport.getSchedulerReportDTO(reportDTO: ReportDTO): SchedulerReportDTO {
    return SchedulerReportDTO(
        id = id,
        personId = personId,
        report = reportDTO,
        reportContext = getEnumReportContextService(context!!)
    )
}

fun getEnumReportContextService(context: EnumReportContext): EnumReportContextService {
    return when (context) {
        EnumReportContext.SCHEDULERS_REPORT -> EnumReportContextService.SCHEDULERS_REPORT
    }
}

fun getEnumReportContext(context: EnumReportContextService): EnumReportContext {
    return when (context) {
        EnumReportContextService.SCHEDULERS_REPORT -> EnumReportContext.SCHEDULERS_REPORT
    }
}
