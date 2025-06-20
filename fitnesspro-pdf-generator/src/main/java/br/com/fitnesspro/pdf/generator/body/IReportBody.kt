package br.com.fitnesspro.pdf.generator.body

import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.common.IPreparable
import br.com.fitnesspro.pdf.generator.session.IReportSession

interface IReportBody<FILTER: Any>: IPreparable<FILTER> {

    val sessions: MutableList<IReportSession<FILTER>>
    val filter: FILTER

    suspend fun draw(pageManager: IPageManager, yStart: Float)

    override suspend fun prepare(filter: FILTER) {
        super.prepare(filter)

        sessions.forEach { it.prepare(filter) }
    }
}