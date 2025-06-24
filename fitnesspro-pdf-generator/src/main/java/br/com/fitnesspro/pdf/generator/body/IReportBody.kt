package br.com.fitnesspro.pdf.generator.body

import br.com.fitnesspro.pdf.generator.common.IDrawable
import br.com.fitnesspro.pdf.generator.common.IPreparable
import br.com.fitnesspro.pdf.generator.session.IReportSession

interface IReportBody<FILTER: Any>: IPreparable<FILTER>, IDrawable {
    val sessions: List<IReportSession<FILTER>>
    val filter: FILTER?

    fun addSession(session: IReportSession<FILTER>)
}