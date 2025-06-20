package br.com.fitnesspro.pdf.generator.components

import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.common.IPreparable

interface IReportComponent<FILTER: Any>: IPreparable<FILTER> {
    suspend fun draw(pageManager: IPageManager, yStart: Float): Float
}