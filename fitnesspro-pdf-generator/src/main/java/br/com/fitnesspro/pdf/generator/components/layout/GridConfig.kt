package br.com.fitnesspro.pdf.generator.components.layout

/**
 * Classe que armazena diversas informações utilizadas no [LayoutGridComponent] para distribuição
 * das colunas. A ideia é centralizar essas informações em um único lugar para facilitar o acesso,
 * mantendo organizado.
 *
 * @param pageWidth Largura da página.
 * @param horizontalPaddingStart Margem horizontal inicial.
 * @param columnSpacing Espaçamento entre as colunas.
 * @param columnWidth Largura de cada coluna.
 * @param paddingTop Margem superior.
 *
 * @author Nikolas Luiz Schmitt
 */
data class GridConfig(
    val pageWidth: Float,
    val horizontalPaddingStart: Float,
    val columnSpacing: Float,
    val columnWidth: Float,
    val paddingTop: Float
)