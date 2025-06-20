package br.com.fitnesspro.pdf.generator.components.gallery

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.components.IReportComponent
import kotlin.properties.Delegates

class GalleryComponent<FILTER: Any>: IReportComponent<FILTER> {

    private lateinit var items: List<GalleryItem>
    private var columns by Delegates.notNull<Int>()

    override suspend fun prepare(filter: FILTER) {
        super.prepare(filter)
    }

    override fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, yStart: Int): Float {
        return 0f
    }
}