package br.com.fitnesspro.pdf.generator.header

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.common.IPreparable

interface IReportHeader<FILTER: Any>: IPreparable<FILTER> {

    fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, pageNumbers: Int)
}