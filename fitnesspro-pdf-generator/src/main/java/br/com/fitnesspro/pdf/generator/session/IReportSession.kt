package br.com.fitnesspro.pdf.generator.session

import br.com.fitnesspro.pdf.generator.common.IDrawable
import br.com.fitnesspro.pdf.generator.common.IPreparable

/**
 * Interface para representar uma sessão do relatório. É nas sessões que os elementos do relatório
 * são agrupados.
 *
 * @author Nikolas Luiz Schmitt
 */
interface IReportSession<FILTER: Any>: IPreparable<FILTER>, IDrawable {

    /**
     * Deve retornar se a sessão deve ser renderizada ou não. Por padrão é sempre renderizada.
     */
    fun shouldRender(filter: FILTER): Boolean = true
}