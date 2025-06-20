package br.com.fitnesspro.pdf.generator.components.table

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.components.IReportComponent

class TableComponent<FILTER: Any>: IReportComponent<FILTER> {

    lateinit var columns: List<Column>
    lateinit var rows: List<List<String>>

    override suspend fun prepare(filter: FILTER) {
        super.prepare(filter)

        // TODO - Buscar os dados da tabela para carregar as linhas e colunas
    }

    override suspend fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, yStart: Float): Float {
        TODO("Not yet implemented")
    }
}