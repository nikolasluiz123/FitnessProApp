package br.com.fitnesspro.pdf.generator.session

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument

abstract class AbstractReportSession<FILTER: Any>: IReportSession<FILTER> {

    override fun shouldRender(filter: FILTER): Boolean {
        // TODO - Fazer a logica padrao para exibir somente se tiver dados
        return true
    }

    override fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, yStart: Int) {
        // TODO - Desenhar o titulo e a linha abaixo dele aqui
    }
}