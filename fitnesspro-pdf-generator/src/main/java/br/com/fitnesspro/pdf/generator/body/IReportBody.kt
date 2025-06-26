package br.com.fitnesspro.pdf.generator.body

import br.com.fitnesspro.pdf.generator.common.IDrawable
import br.com.fitnesspro.pdf.generator.common.IPreparable
import br.com.fitnesspro.pdf.generator.session.IReportSession

/**
 * Interface para representar o 'corpo' do relatório, servindo como algo que vai centralizar o
 * conteúdo.
 *
 * @author Nikolas Luiz Schmitt
 */
interface IReportBody<FILTER: Any>: IPreparable<FILTER>, IDrawable {

    /**
     * Lista das sessões contidas no relatório. É uma forma muito comum de organizar as informações
     * utilizando títulos para agrupar os elementos.
     */
    val sessions: List<IReportSession<FILTER>>

    /**
     * Filtro que será propagado para todas as partes do relatório, possibilitando a implementação
     * das regras e filtros nas queries.
     */
    val filter: FILTER?

    /**
     * Deve adicionar uma sessão ao relatório.
     *
     * @param session Instância da sessão a ser adicionada.
     */
    fun addSession(session: IReportSession<FILTER>)
}