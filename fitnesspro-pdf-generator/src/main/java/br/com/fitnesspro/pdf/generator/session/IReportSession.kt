package br.com.fitnesspro.pdf.generator.session

import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.common.IPreparable

interface IReportSession<FILTER: Any>: IPreparable<FILTER> {

    fun shouldRender(filter: FILTER): Boolean = true

    suspend fun draw(pageManager: IPageManager, yStart: Float): Float
}