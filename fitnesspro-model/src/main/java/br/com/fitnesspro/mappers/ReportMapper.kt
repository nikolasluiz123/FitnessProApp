package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.model.enums.EnumTransmissionState.PENDING
import br.com.fitnesspro.model.enums.EnumTransmissionState.TRANSMITTED
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
        active = active
    )

    id?.let { model.id = it }

    return model
}

fun TOSchedulerReport.getSchedulerReport(): SchedulerReport {
    val model = SchedulerReport(
        personId = personId,
        reportId = reportId,
        context = context,
        active = active
    )

    id?.let { model.id = it }

    return model
}

fun ReportDTO.getReport(): Report {
    return Report(
        id = id!!,
        name = name,
        extension = extension,
        filePath = filePath,
        date = date!!,
        kbSize = kbSize,
        active = active,
        transmissionState = TRANSMITTED,
        storageTransmissionState = if (storageTransmissionDate != null) TRANSMITTED else PENDING,
        storageUrl = storageUrl,
        storageTransmissionDate = storageTransmissionDate
    )
}

fun SchedulerReportDTO.getSchedulerReport(): SchedulerReport {
    return SchedulerReport(
        id = id!!,
        transmissionState = TRANSMITTED,
        personId = personId,
        reportId = reportId,
        context = getEnumReportContext(reportContext!!),
        active = active
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
        active = active,
        storageUrl = storageUrl,
        storageTransmissionDate = storageTransmissionDate
    )
}

fun SchedulerReport.getSchedulerReportDTO(): SchedulerReportDTO {
    return SchedulerReportDTO(
        id = id,
        personId = personId,
        reportId = reportId,
        reportContext = getEnumReportContextService(context!!),
        active = active
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
