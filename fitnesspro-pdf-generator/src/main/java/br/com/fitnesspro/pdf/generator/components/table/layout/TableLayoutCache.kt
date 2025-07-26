package br.com.fitnesspro.pdf.generator.components.table.layout

/**
 * Classe que representa o layout da tabela que vamos manter em cache.
 */
data class TableLayoutCache(val header: RowLayout, val rows: List<RowLayout>)