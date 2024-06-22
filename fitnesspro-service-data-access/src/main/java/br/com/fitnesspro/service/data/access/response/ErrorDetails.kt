package br.com.fitnesspro.service.data.access.response

data class ErrorDetails(
    val code: Int,
    val message: String,
    val errors: Map<String, List<String>>? = null,
    val details: String? = null
)
