package br.com.fitnesspro.pdf.generator.common

/**
 * Interface que representa alguma parte do relatório que pode (opcionalmente) possuir um processo
 * de 'preparação', antes de ser desenhado.
 *
 * @author Nikolas Luiz Schmitt
 */
interface IPreparable<FILTER: Any> {

    /**
     * Deve realizar todos os processos necessários para possibilitar o desenho, sejam queries ou
     * outros tipos de processamentos.
     */
    suspend fun prepare(filter: FILTER) = Unit
}