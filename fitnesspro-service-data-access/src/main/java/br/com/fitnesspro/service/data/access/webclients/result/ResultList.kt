package br.com.fitnesspro.service.data.access.webclients.result

import br.com.fitnesspro.service.data.access.response.ErrorDetails

class ResultList<T>(
    val data: List<T>,
    val error: ErrorDetails? = null
)