package br.com.fitnesspro.to

import java.time.LocalDateTime

data class TOReport(
    override var id: String? = null,
    var name: String? = null,
    var extension: String? = null,
    var filePath: String? = null,
    var date: LocalDateTime? = null,
    var kbSize: Long? = null
): BaseTO