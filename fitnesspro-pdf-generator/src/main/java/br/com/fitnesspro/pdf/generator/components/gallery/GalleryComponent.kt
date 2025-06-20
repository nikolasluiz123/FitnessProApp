package br.com.fitnesspro.pdf.generator.components.gallery

import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.components.IReportComponent
import kotlin.properties.Delegates

class GalleryComponent<FILTER: Any>: IReportComponent<FILTER> {

    private lateinit var items: List<GalleryItem>
    private var columns by Delegates.notNull<Int>()

    override suspend fun prepare(filter: FILTER) {
        super.prepare(filter)
    }

    override suspend fun draw(pageManager: IPageManager, yStart: Float): Float {
        return 0f
    }
}