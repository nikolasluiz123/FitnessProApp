package br.com.fitnesspro.mappers

import br.com.android.room.toolkit.model.enums.EnumDownloadState
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.core.android.utils.media.FileUtils
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.model.general.report.SchedulerReport
import br.com.fitnesspro.model.general.report.WorkoutReport
import br.com.fitnesspro.shared.communication.dtos.general.ReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.SchedulerReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.WorkoutReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.ISchedulerReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IWorkoutReportDTO
import br.com.fitnesspro.to.TOReport
import br.com.fitnesspro.to.TOSchedulerReport
import br.com.fitnesspro.to.TOWorkoutReport
import br.com.fitnesspro.shared.communication.enums.report.EnumReportContext as EnumReportContextService

fun TOReport.getReport(): Report {
    val model = Report(
        name = name,
        extension = extension,
        filePath = filePath,
        date = date!!,
        kbSize = kbSize,
        active = active,
        storageDownloadState = getReportDownloadState(filePath)
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

fun TOWorkoutReport.getWorkoutReport(): WorkoutReport {
    val model = WorkoutReport(
        personId = personId,
        reportId = reportId,
        workoutId = workoutId,
        context = context,
        active = active
    )

    id?.let { model.id = it }

    return model
}

fun IReportDTO.getReport(): Report {
    return Report(
        id = id!!,
        name = name,
        extension = extension,
        filePath = filePath,
        date = date!!,
        kbSize = kbSize,
        active = active,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        storageTransmissionState = if (storageTransmissionDate != null) EnumTransmissionState.TRANSMITTED else EnumTransmissionState.PENDING,
        storageUrl = storageUrl,
        storageTransmissionDate = storageTransmissionDate,
        storageDownloadState = getReportDownloadState(filePath)
    )
}

fun ISchedulerReportDTO.getSchedulerReport(): SchedulerReport {
    return SchedulerReport(
        id = id!!,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        personId = personId,
        reportId = reportId,
        context = getEnumReportContext(reportContext!!),
        active = active
    )
}

fun IWorkoutReportDTO.getWorkoutReport(): WorkoutReport {
    return WorkoutReport(
        id = id!!,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        workoutId = workoutId,
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

fun WorkoutReport.getWorkoutReportDTO(): WorkoutReportDTO {
    return WorkoutReportDTO(
        id = id,
        workoutId = workoutId,
        personId = personId,
        reportId = reportId,
        reportContext = getEnumReportContextService(context!!),
        active = active
    )
}

fun getEnumReportContextService(context: EnumReportContext): EnumReportContextService {
    return when (context) {
        EnumReportContext.SCHEDULERS_REPORT -> EnumReportContextService.SCHEDULERS_REPORT
        EnumReportContext.WORKOUT_REGISTER_EVOLUTION -> EnumReportContextService.WORKOUT_REGISTER_EVOLUTION
    }
}

fun getEnumReportContext(context: EnumReportContextService): EnumReportContext {
    return when (context) {
        EnumReportContextService.SCHEDULERS_REPORT -> EnumReportContext.SCHEDULERS_REPORT
        EnumReportContextService.WORKOUT_REGISTER_EVOLUTION -> EnumReportContext.WORKOUT_REGISTER_EVOLUTION
    }
}

private fun getReportDownloadState(filePath: String?): EnumDownloadState {
    val exists = filePath?.let { FileUtils.getFileExists(it) } ?: false
    return if (exists) EnumDownloadState.DOWNLOADED else EnumDownloadState.PENDING
}
