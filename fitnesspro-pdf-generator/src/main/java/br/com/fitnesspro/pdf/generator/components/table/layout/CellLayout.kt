package br.com.fitnesspro.pdf.generator.components.table.layout

import android.text.StaticLayout
import br.com.fitnesspro.pdf.generator.components.table.enums.VerticalAlign

/**
 * Classe que representa o layout da célula de uma tabela.
 */
data class CellLayout(val layout: StaticLayout, val verticalAlign: VerticalAlign)