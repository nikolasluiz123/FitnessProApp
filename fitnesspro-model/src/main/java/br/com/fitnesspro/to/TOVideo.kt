package br.com.fitnesspro.to

import br.com.fitnesspro.core.extensions.dateTimeNow
import java.time.LocalDateTime
import java.time.ZoneId

data class TOVideo(
    override var id: String? = null,
    var extension: String? = null,
    var filePath: String? = null,
    var date: LocalDateTime = dateTimeNow(ZoneId.systemDefault()),
    var kbSize: Long? = null,
    var seconds: Long? = null,
    var width: Int? = null,
    var height: Int? = null
): BaseTO