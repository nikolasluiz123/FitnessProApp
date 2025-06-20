package br.com.fitnesspro.pdf.generator.common

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.enums.EnumPageSize
import br.com.fitnesspro.pdf.generator.footer.IReportFooter
import br.com.fitnesspro.pdf.generator.header.IReportHeader
import br.com.fitnesspro.pdf.generator.utils.Margins

class PageManager(
    private val document: PdfDocument,
    private val pageSize: EnumPageSize,
    private val header: IReportHeader<*>,
    private val footer: IReportFooter<*>
) : IPageManager {

    override lateinit var canvas: Canvas
    override lateinit var pageInfo: PdfDocument.PageInfo
    private lateinit var currentPage: PdfDocument.Page

    private var pageNumber = 0

    override var currentY: Float = 0f

    private val marginBottom = Margins.MARGIN_32.toFloat()

    suspend fun start() {
        startNewPage()
        currentY = drawHeader()
    }

    override fun finish() {
        drawFooter()
        document.finishPage(currentPage)
    }

    override suspend fun ensureSpace(currentY: Float, heightNeeded: Float): Float {
        return if (!checkSpace(currentY, heightNeeded)) {
            startNewPage()
            drawHeader()
        } else {
            currentY
        }
    }

    override fun checkSpace(currentY: Float, heightNeeded: Float): Boolean {
        val bottomLimit = pageInfo.pageHeight.toFloat() - marginBottom
        return (currentY + heightNeeded) <= bottomLimit
    }

    private fun startNewPage() {
        if (::currentPage.isInitialized) {
            drawFooter()
            document.finishPage(currentPage)
        }

        pageNumber++
        pageInfo = PdfDocument.PageInfo.Builder(pageSize.width, pageSize.height, pageNumber).create()
        currentPage = document.startPage(pageInfo)
        canvas = currentPage.canvas
        currentY = 0f
    }

    private suspend fun drawHeader(): Float {
        return header.draw(canvas, pageInfo, pageNumber)
    }

    private fun drawFooter() {
        footer.draw(canvas, pageInfo, pageNumber)
    }
}