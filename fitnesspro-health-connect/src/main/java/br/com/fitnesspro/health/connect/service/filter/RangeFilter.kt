package br.com.fitnesspro.health.connect.service.filter

import java.time.Instant

/**
 * Define um filtro de intervalo de tempo (início e fim) para consultas ao Health Connect.
 *
 * @property start O [Instant] de início do intervalo (inclusivo).
 * @property end O [Instant] de fim do intervalo (inclusivo).
 *
 * @author Nikolas Luiz Schmitt
 */
data class RangeFilter(
    val start: Instant,
    val end: Instant
)