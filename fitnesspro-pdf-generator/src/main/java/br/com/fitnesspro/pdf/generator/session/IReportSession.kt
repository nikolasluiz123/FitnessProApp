package br.com.fitnesspro.pdf.generator.session

import br.com.fitnesspro.pdf.generator.common.IDrawable
import br.com.fitnesspro.pdf.generator.common.IPreparable

interface IReportSession<FILTER: Any>: IPreparable<FILTER>, IDrawable {
    fun shouldRender(filter: FILTER): Boolean = true
}