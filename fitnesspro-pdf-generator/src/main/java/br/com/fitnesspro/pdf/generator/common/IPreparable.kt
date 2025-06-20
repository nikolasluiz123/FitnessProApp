package br.com.fitnesspro.pdf.generator.common

interface IPreparable<FILTER: Any> {
    suspend fun prepare(filter: FILTER) = Unit
}