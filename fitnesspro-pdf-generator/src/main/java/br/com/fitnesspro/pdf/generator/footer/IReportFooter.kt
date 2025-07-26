package br.com.fitnesspro.pdf.generator.footer

import br.com.fitnesspro.pdf.generator.common.IDrawable
import br.com.fitnesspro.pdf.generator.common.IPreparable

/**
 * Interface para representar a parte inferior do relatório, a qual chamamos de Footer.
 */
interface IReportFooter<FILTER: Any>: IPreparable<FILTER>, IDrawable