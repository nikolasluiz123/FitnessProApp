package br.com.fitnesspro.pdf.generator.header

import br.com.fitnesspro.pdf.generator.common.IDrawable
import br.com.fitnesspro.pdf.generator.common.IPreparable

/**
 * Interface para representar a parte superior normalmente presente em relatórios, a qual chamamos
 * de Header.
 *
 * @author Nikolas Luiz Schmitt
 */
interface IReportHeader<FILTER: Any>: IPreparable<FILTER>, IDrawable