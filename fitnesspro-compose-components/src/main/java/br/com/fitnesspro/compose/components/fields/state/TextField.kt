package br.com.fitnesspro.compose.components.fields.state

/**
 * Classe que representa um campo de formulário, contendo os atributos que sempre serão necessários e
 * evita que as classes UIState das telas fiquem cheias de atributos.
 *
 * @property value Valor do campo
 * @property onChange Função que será chamada quando o valor do campo for alterado
 * @property errorMessage Mensagem de erro a ser exibida quando o valor do campo for inválido de acordo com as regras
 * da tela.
 *
 * @author Nikolas Luiz Schmitt
 */
data class TextField(
    override val value: String = "",
    override val onChange: (String) -> Unit = { },
    override val errorMessage: String = ""
): ITextField