package br.com.fitnesspro.pdf.generator.common

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument

/**
 * Interface que representa um gerenciador de páginas do relatório PDF.
 *
 * Ao desenhar em um documento composto por páginas, é necessário controlar a posição em que os
 * elementos serão desenhados, principalmente na altura (eixo Y).
 *
 * A ideia principal aqui é que seja possível as partes do relatório que são desenhadas possam delegar
 * para alguém que consiga responder se há espaço para desenhar, se precisa de uma nova página e
 * outras responsabilidades nesse sentido.
 *
 * @author Nikolas Luiz Schmitt
 */
interface IPageManager {

    /**
     * A API do [PdfDocument] utiliza um [Canvas] para cada página, sempre que uma página é finalizada
     * o canvas é descartado. Aqui é armazenado o canvas da página atual.
     */
    val canvas: Canvas

    /**
     * Objeto que contém informações sobre a página atual. Pode ser útil em [IDrawable.draw] para
     * realizar calculos com a dimensão da página, por exemplo.
     */
    val pageInfo: PdfDocument.PageInfo

    /**
     * Armazena a posição no eixo Y em que o próximo elemento deve ser desenhado.
     */
    var currentY: Float

    /**
     * Deve avaliar se há espaço suficiente na página para desenhar o elemento. Se houver, apenas
     * entrega a posição Y sem alterações. Se não houver, inicia uma nova página e retorna a posição
     * Y onde deve ser desenhado em uma nova página.
     *
     * @param currentY Posição atual no eixo Y.
     * @param heightNeeded Altura necessária para desenhar o elemento.
     */
    suspend fun ensureSpace(currentY: Float, heightNeeded: Float): Float

    /**
     * Retorna se há espaço suficiente na página para desenhar o elemento.
     *
     * @param currentY Posição atual no eixo Y.
     * @param heightNeeded Altura necessária para desenhar o elemento.
     */
    fun hasAvailableSpace(currentY: Float, heightNeeded: Float): Boolean

    /**
     * Deve realizar os processos necessários para que seja criada uma nova página.
     */
    suspend fun start()

    /**
     * Deve realizar os processos necessários para que seja finalizada a página atual.
     */
    suspend fun finish()
}