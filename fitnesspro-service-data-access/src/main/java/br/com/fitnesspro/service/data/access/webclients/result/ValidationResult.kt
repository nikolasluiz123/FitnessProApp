package br.com.fitnesspro.service.data.access.webclients.result

import br.com.fitnesspro.service.data.access.dto.interfaces.IEnumDTOValidationFields

/**
 * Classe que representa o resultado da validação de um DTO
 */
sealed class ValidationResult {

    /**
     * Object para representar sucesso na validação
     */
    data object Success : ValidationResult()

    /**
     * Object para representar erro na validação.
     *
     * @param fieldErrors Quando ocorrer um erro em algum campo do DTO que foi enviado, os erros
     * são retornados aqui usando uma indexação pelo enumerador, onde o valor é a string da mensagem
     * de validação definida no serviço.
     *
     * @param message Quando ocorrer algum erro que não é relacionado aos campos em si, será preenchido
     * esse campo com a mensagem de erro.
     *
     * @param details Quando [message] for preenchido é possível que haja detalhes desse erro, normalmente
     * não precisam ser exibidos para o usuário mas podem ser logados.
     */
    data class Error<ENUM>(
        val fieldErrors: List<Pair<ENUM, String>> = emptyList(),
        val message: String? = null,
        val details: String? = null
    ) : ValidationResult() where ENUM : Enum<ENUM>, ENUM : IEnumDTOValidationFields
}