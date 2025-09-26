package br.com.fitnesspro.pdf.generator.report

import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.body.IReportBody
import br.com.fitnesspro.pdf.generator.common.PageManager
import br.com.fitnesspro.pdf.generator.enums.EnumPageSize
import br.com.fitnesspro.pdf.generator.footer.IReportFooter
import br.com.fitnesspro.pdf.generator.header.IReportHeader

/**
 * Classe abstrata que representa um relatório PDF.
 *
 * Essa classe é responsável por definir a estrutura básica de um relatório PDF, contendo
 * os componentes de cabeçalho, corpo e rodapé.
 *
 * @param filter Filtro utilizado para gerar o relatório. Pode conter tanto filtros para usar em queries,
 * quanto condições para exibir ou não partes do relatório.
 *
 * @author Nikolas Luiz Schmitt
 */
abstract class AbstractPDFReport<FILTER: Any>(var filter: FILTER) {

    protected lateinit var header: IReportHeader<FILTER>
    protected lateinit var body: IReportBody<FILTER>
    protected lateinit var footer: IReportFooter<FILTER>

    /**
     * Função que deve ser utilizada para inicializar os componentes do relatório. Em resumo, é aqui
     * onde devem ser criados os objetos de cabeçalho, corpo e rodapé.
     */
    protected abstract suspend fun initialize()

    /**
     * Função que executa [br.com.fitnesspro.pdf.generator.common.IPreparable.prepare] de cada uma
     * das partes do relatório, sendo essa uma ação que ocorre em cascata.
     *
     * Ao fim da execução todas os dados de todas as partes do relatório devem estar carregados e o
     * relatório estará pronto para ser desenhado.
     */
    protected suspend fun prepare() {
        header.prepare(filter)
        body.prepare(filter)
        footer.prepare(filter)
    }

    /**
     * Principal função da implementação, ela é responsável pelo processo de geração do relatório.
     *
     * @param document Instância da representação do documento PDF da API usada para gerar o relatório.
     * @param pageSize Tamanho da página do relatório.
     *
     * @see br.com.fitnesspro.pdf.generator.common.PageManager
     */
    suspend fun generate(document: PdfDocument, pageSize: EnumPageSize) {
        initialize()
        prepare()

        val pageManager = PageManager(document, pageSize, header, footer)
        pageManager.start()

        body.draw(pageManager, pageManager.currentY)

        pageManager.finish()
    }
}