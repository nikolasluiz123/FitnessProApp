package br.com.fitnesspro.scheduler.reports

import android.content.Context
import androidx.core.graphics.drawable.toBitmap
import br.com.fitnesspro.pdf.generator.header.AbstractReportHeader
import br.com.fitnesspro.pdf.generator.R as PdfGeneratorRes
import br.com.fitnesspro.scheduler.R as SchedulerRes

class SchedulerReportHeader(
    private val context: Context
): AbstractReportHeader<SchedulerFilter>() {

    override suspend fun prepare(filter: SchedulerFilter) {
        super.prepare(filter)

        this.title = context.getString(SchedulerRes.string.scheduler_report_header_title)
        this.bitmap = context.getDrawable(PdfGeneratorRes.drawable.default_report_logo)!!.toBitmap()
    }
}