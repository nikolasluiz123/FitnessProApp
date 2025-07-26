package br.com.fitnesspro.pdf.generator.common

/**
 * Interface para representar qualquer parte do relatório que pode ser desenhada. Para todos esses
 * elementos considerados como 'desenhaveis' sempre devemos calcular a sua altura total e, somente
 * após isso, desenhá-los.
 *
 * @author Nikolas Luiz Schmitt
 */
interface IDrawable {

    /**
     * Deve calcular a altura total do elemento. Essa altura tem como objetivo principal indicar se
     * vai quebrar a página ou não.
     *
     * É uma boa prática armazenar esse valor e, dependendo do cenário, criar um tipo de cache.
     *
     * @param pageManager Gerenciador de páginas do relatório.
     */
    suspend fun measureHeight(pageManager: IPageManager): Float

    /**
     * Deve desenhar o elemento na página, iniciando na posição Y indicada.
     *
     * @param pageManager Gerenciador de páginas do relatório.
     * @param yStart Posição Y em que o elemento deve ser desenhado.
     */
    suspend fun draw(pageManager: IPageManager, yStart: Float): Float
}