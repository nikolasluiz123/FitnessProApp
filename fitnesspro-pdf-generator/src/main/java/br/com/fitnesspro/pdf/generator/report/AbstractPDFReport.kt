package br.com.fitnesspro.pdf.generator.report

import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.body.IReportBody
import br.com.fitnesspro.pdf.generator.enums.EnumPageSize
import br.com.fitnesspro.pdf.generator.footer.IReportFooter
import br.com.fitnesspro.pdf.generator.header.IReportHeader

abstract class AbstractPDFReport<FILTER: Any>(var filter: FILTER) {

    protected lateinit var header: IReportHeader<FILTER>
    protected lateinit var body: IReportBody<FILTER>
    protected lateinit var footer: IReportFooter<FILTER>

    protected abstract fun initialize()

    protected suspend fun prepare() {
        header.prepare(filter)
        body.prepare(filter)
        footer.prepare(filter)
    }

    suspend fun generate(document: PdfDocument, pageSize: EnumPageSize) {
        initialize()
        prepare()

        val pageInfo = PdfDocument.PageInfo.Builder(pageSize.width, pageSize.height, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val pageNumber = 1

        header.draw(canvas, pageInfo, pageNumber)
        body.draw(canvas, pageInfo, pageNumber)
        footer.draw(canvas, pageInfo, pageNumber)

        document.finishPage(page)
    }
}